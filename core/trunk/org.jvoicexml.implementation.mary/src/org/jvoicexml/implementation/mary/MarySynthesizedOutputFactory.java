package org.jvoicexml.implementation.mary;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**implementation of a.
* {@link org.jvoicexml.implementation.ResourceFactory} for the
* {@link SynthesizedOutput} based on MaryTTS
**/

public class MarySynthesizedOutputFactory implements
    ResourceFactory<SynthesizedOutput> {

    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MarySynthesizedOutput.class);

    /** Number of instances that this factory will create. */
    private  int instances;

    /** Type of the created resources. */
    private String type;

    @Override
    public final SynthesizedOutput createResource() throws NoresourceError {

        final MarySynthesizedOutput output = new MarySynthesizedOutput();
        output.setType(type);
        return output;
    }

    @Override
    public final int getInstances() {
        // TODO Auto-generated method stub
        return instances;
    }

    @Override
    public final Class<SynthesizedOutput> getResourceType() {
        return  SynthesizedOutput.class;
    }

    @Override
    public final String getType() {
        return  type;
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *            Number of instances to create.
     */
    public final void setInstances(final int number) {
        instances = number;
    }

    /**
     * Sets the type of the resource.
     *
     * @param resourceType
     *            type of the resource.
     */

    public final void setType(final String resourceType) {
        type = resourceType;
      }


}
