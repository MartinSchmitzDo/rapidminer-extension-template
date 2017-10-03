package com.rapidminer.extension.rmSmile;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;

import java.text.ParseException;

/**
 * Created by mschmitz on 03.10.17.
 */
public class ExampleSetConverter extends Operator {

    InputPort exa = getInputPorts().createPort("exa",ExampleSet.class);

    public ExampleSetConverter(OperatorDescription description){
        super(description);
    };

    public void doWork() throws UserError {
        LittleSmile parser = new LittleSmile();
        try {
            parser.getDataSet(exa.getData(ExampleSet.class));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
