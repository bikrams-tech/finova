package bikram.businessmanager.service;

public  class ServiceProvider {
    private static final ServiceContainer serviceContainer = new ServiceContainer();
    public static ServiceContainer services(){return serviceContainer;}
}
