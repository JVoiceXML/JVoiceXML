package org.jvoicexml.implementation.mary;



import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;


public class MarySpokenInputFactory implements ResourceFactory<SpokenInput> {

    private int instances;
    private String type;

    @Override
    public SpokenInput createResource() throws NoresourceError {
        
        return new MarySpokenInput();
    }

    @Override
    public int getInstances() {
        return instances;
 
    }

    @Override
    public Class<SpokenInput> getResourceType() {
       return SpokenInput.class;
        
    }

    @Override
    public String getType() {
        return type;
       
    }
    
    public void setType(String type) {
       this.type=type;
       
    }
    
    
    public void setInstances(final int number) {
        instances = number;
    }

 
}

