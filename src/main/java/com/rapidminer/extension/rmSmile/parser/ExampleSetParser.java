package com.rapidminer.extension.rmSmile.parser;

import com.rapidminer.example.*;
import com.rapidminer.example.utils.ExampleSetBuilder;
import com.rapidminer.operator.UserError;
import smile.data.*;
import smile.data.Attribute;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mschmitz on 03.10.17.
 */
public class ExampleSetParser {
    public ExampleSetParser(){};
    private List<Attribute> smileAtts = new ArrayList<>();
    private Attributes rmAtt;

    public AttributeDataset getDataSet(ExampleSet exa) throws ParseException {
        rmAtt = exa.getAttributes();
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


}
