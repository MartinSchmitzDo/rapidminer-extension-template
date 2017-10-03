package com.rapidminer.extension.rmSmile;

import com.rapidminer.example.*;

import com.rapidminer.operator.Operator;

import smile.data.*;
import smile.data.Attribute;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This class provides helper functions to convert example sets into data sets and get data rows in the right order
 *
 * Created by mschmitz on 03.10.17.
 */
public class LittleSmile {
    public LittleSmile(){};
    private List<Attribute> smileAtts = new ArrayList<>();
    private Attributes rmAtt;




    public AttributeDataset getDataSet(ExampleSet exa) throws ParseException {
        rmAtt = (Attributes) exa.getAttributes().clone();

        for(com.rapidminer.example.Attribute a : exa.getAttributes()){
            if(a.isNumerical()){
                smileAtts.add(new NumericAttribute(a.getName()));
            }
            else if(a.isNominal()){
                smileAtts.add(new StringAttribute(a.getName()));
            }
            else if(a.isDateTime()){
                smileAtts.add(new DateAttribute(a.getName()));
            }

        }

        Attribute response = null;
        if(exa.getAttributes().getLabel().isNumerical()){
            response = new NumericAttribute(exa.getAttributes().getLabel().getName());
        }
        if(exa.getAttributes().getLabel().isNominal()){
            response = new NominalAttribute(exa.getAttributes().getLabel().getName());
        }

        Attribute[] attr = new Attribute[smileAtts.size()];
        smileAtts.toArray(attr);


        AttributeDataset ds = new AttributeDataset( "name",attr,response);
        for(Example e : exa){
            double[] x = new double[attr.length];
            int i = 0;
            for(com.rapidminer.example.Attribute a : e.getAttributes()){
                x[i] = attr[i].valueOf(e.getValueAsString(a));
                i++;
            }
            //if(e.getAttributes().findRoleByName("label"){

                com.rapidminer.example.Attribute label = e.getAttributes().getLabel();
                if(label.isNominal()){
                    int y = label.getMapping().mapString(e.getNominalValue(label));

                    ds.add(x,y);
                }
                else{
                    double y = response.valueOf(e.getValueAsString(e.getAttributes().getLabel()));
                    ds.add(x,y);
                }

        }


        return ds;
    }

    public com.rapidminer.example.Attributes getRMAtts(){
        return rmAtt;
    };

    public double[] getAsRow(ExampleSet exa, int i ){
        double[] row = new double[rmAtt.size()];
        int aCounter = 0;
        for(com.rapidminer.example.Attribute a : rmAtt){
            com.rapidminer.example.Attribute attributeInThisExample = exa.getAttributes().get(a.getName());
            // if this is used on another exa, we need to search by name
            row[aCounter] = exa.getExample(i).getValue(attributeInThisExample);
            ++aCounter;

        }
        return row;
    }


}
