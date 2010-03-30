package org.jvoicexml.implementation.mary;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.Telephony;

public class MaryTelephonyFactory 
    implements ResourceFactory<Telephony> {
    
        /** Number of instances that this factory will create. */
        private int instances;

        /**
         * Constructs a new object.
         */
        public MaryTelephonyFactory() {
        }

        /**
         * {@inheritDoc}
         */
        public Telephony createResource()
            throws NoresourceError {

            return new MaryTelephony();
        }

        /**
         * Sets the number of instances that this factory will create.
         * @param number Number of instances to create.
         */
        public void setInstances(final int number) {
            instances = number;
        }

        /**
         * {@inheritDoc}
         */
        public int getInstances() {
            return instances;
        }

        /**
         * {@inheritDoc}
         */
        public String getType() {
            return "maryTTS";
        }

        /**
         * {@inheritDoc}
         */
        public Class<Telephony> getResourceType() {
            return Telephony.class;
        }
}

