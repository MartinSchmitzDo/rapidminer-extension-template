package com.rapidminer.extension.rmSmile.clusterer;

import com.rapidminer.example.*;
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

import org.bouncycastle.crypto.modes.EAXBlockCipher;
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

        ExampleSet exa = exaInput.getData(ExampleSet.class);

        // there might be a nicer version
        try {
            ds = parser.getDataSet(exa);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ExampleSet e  = exaInput.getData(ExampleSet.class);
        double[][] valueArray = new double[e.size()][e.getAttributes().size()];

        KMeans kMeans = new KMeans(ds.toArray(valueArray),3,10);

        // learning done

        Attribute clusterAtt = AttributeFactory.createAttribute("clusterid", Ontology.NOMINAL);
        exa.getAttributes().addRegular(clusterAtt);
        exa.getExampleTable().addAttribute(clusterAtt);
        getLogger().log(Level.SEVERE, String.valueOf(parser.getRMAtts().size()));
        for(int i = 0; i<exa.size();++i){
            int prediction = kMeans.predict(parser.getAsRow(exa,i));
            exa.getExample(i).setValue(clusterAtt,"cluster_"+String.valueOf(prediction));

        }
        exa.getAttributes().setCluster(clusterAtt);
        exaOut.deliver(exa);


    }
}
