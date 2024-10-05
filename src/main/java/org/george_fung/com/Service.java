package org.george_fung.com;

public interface Service {
    /**
     * Start-up service
     * @throws Exception
     */
    void startService() throws Exception;

    /**
     * End of service
     */
    void stopService();
}
