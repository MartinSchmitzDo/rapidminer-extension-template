package com.rapidminer.extension.rmSmile.clusterer;

import com.rapidminer.example.*;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.extension.rmSmile.LittleSmile;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.Parameters;
import com.rapidminer.tools.Ontology;

import smile.clustering.KMeans;
import smile.data.AttributeDataset;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by mschmitz on 03.10.17.
 */
public class KMeansClusteringOperator extends Operator {

    InputPort exaInput = getInputPorts().createPort("exa", ExampleSet.class);
    OutputPort exaOut = getOutputPorts().createPort("out");

    public final static String PARAMETER_K = "k";
    public final static String PARAMETER_ITER = "Max Iterations";
    public final static String PARAMETER_RUNS = "Runs";

    public KMeansClusteringOperator(OperatorDescription description){
        super(description);
    }
    public void doWork() throws UserError {
        LittleSmile parser = new LittleSmile();
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

        KMeans kMeans = new KMeans(ds.toArray(valueArray),
                getParameterAsInt(PARAMETER_K),
                getParameterAsInt(PARAMETER_ITER),
                getParameterAsInt(PARAMETER_RUNS)
        );

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

    @Override
    public List<ParameterType> getParameterTypes(){
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(PARAMETER_K,"Number of Clusters",2,Integer.MAX_VALUE,5));
        types.add(new ParameterTypeInt(PARAMETER_ITER,"Number of Iterations",1,Integer.MAX_VALUE,10));
        types.add(new ParameterTypeInt(PARAMETER_RUNS,"Number of Runs",1,Integer.MAX_VALUE,10));
        return types;
    }

}

