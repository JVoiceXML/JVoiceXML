package org.jvoicexml.implementation.mary;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;




public class MarySynthesizedOutputFactory implements ResourceFactory<SynthesizedOutput> {

    
    private static final Logger LOGGER =
        Logger.getLogger(MarySynthesizedOutput.class);
    
    public  int instances;
    public String type;
    
    @Override
    public SynthesizedOutput createResource() throws NoresourceError {
     
        final MarySynthesizedOutput output = new MarySynthesizedOutput();
        output.setType(type);
        return output;
    }

    @Override
    public int getInstances() {
        // TODO Auto-generated method stub
        return instances;
    }

    @Override
    public Class<SynthesizedOutput> getResourceType(){
        return  SynthesizedOutput.class;
    }

    @Override
    public String getType() {
        return  type;
    }
    
    public void setInstances(int instances) { 
      this.instances=instances;
    }
    
    public void setType(String type) {
         this.type=type;
      }
    
    
    
}
