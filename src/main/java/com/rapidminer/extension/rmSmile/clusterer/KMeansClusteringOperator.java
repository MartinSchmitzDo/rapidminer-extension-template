package com.rapidminer.extension.rmSmile.clusterer;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.utils.ExampleSetBuilder;
import com.rapidminer.example.utils.ExampleSets;
import com.rapidminer.extension.rmSmile.parser.ExampleSetParser;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.tools.Ontology;

import smile.clustering.KMeans;
import smile.data.AttributeDataset;
import smile.data.Dataset;
import smile.data.Datum;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Created by mschmitz on 03.10.17.
 */
public class KMeansClusteringOperator extends Operator {

    InputPort exaInput = getInputPorts().createPort("exa", ExampleSet.class);
    OutputPort exaOut = getOutputPorts().createPort("out");

    public KMeansClusteringOperator(OperatorDescription description){
        super(description);
    }
    public void doWork() throws UserError {
        ExampleSetParser parser = new ExampleSetParser();
        AttributeDataset ds = null;
        try {
            ds = parser.getDataSet(exaInput.getData(ExampleSet.class));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ExampleSet e  = exaInput.getData(ExampleSet.class);
        double[][] valueArray = new double[e.size()][e.getAttributes().size()];

        KMeans kMeans = new KMeans(ds.toArray(valueArray),3,10);

        getLogger().log(Level.SEVERE,ds.get(0).x.toString());
        getLogger().log(Level.SEVERE,String.valueOf(kMeans.predict(ds.get(0).x)));

        Attributes atts = parser.getRMAtts();
        List<Attribute> listOfAtt = new ArrayList<>();
        for (Attribute a : atts){
            listOfAtt.add(a);
        }
        Attribute clusterAtt = AttributeFactory.createAttribute("clusterid", Ontology.NOMINAL);
        listOfAtt.add(clusterAtt);

        ExampleSetBuilder builds;
        builds = ExampleSets.from(listOfAtt);

        for(int i = 0; i < ds.size(); ++i){
            double[] row = new double[listOfAtt.size()];
            for(int j = 0; j < listOfAtt.size()-1;++j) {
                row[j] = ds.get(i).x[j];
                getLogger().log(Level.SEVERE,Integer.toString(j));
            }
            row[listOfAtt.size()-1] = clusterAtt.getMapping().mapString(
                    "cluster_"+ Integer.toString( kMeans.predict(ds.get(i).x))
            );

            builds.addRow(row);
        }
        exaOut.deliver(builds.build());


    }
}
