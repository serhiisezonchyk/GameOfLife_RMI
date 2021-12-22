package Server;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerEngine3 extends UnicastRemoteObject implements Compute{
    public ServerEngine3() throws RemoteException {
        super();
    }

    public Object executeTask(Task t) throws RemoteException{
        return t.execute();
    }

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "C:\\Users\\Сергей\\IdeaProjects\\MOIL3\\src\\Server\\rmi.policy\\");
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        String name = "rmi://localhost/Compute3";
        try {
            LocateRegistry.getRegistry(1066);
            Compute engine = new ServerEngine();
            Naming.rebind(name, engine);
            System.out.println("ComputeEngine3 bound");
        } catch(Exception e) {
            System.err.println("ComputeEngine3 exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
