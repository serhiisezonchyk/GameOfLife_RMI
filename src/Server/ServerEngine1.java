package Server;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerEngine1 extends UnicastRemoteObject implements Compute{
    public ServerEngine1() throws RemoteException {
        super();
    }

    public Object executeTask(Task t) throws RemoteException {
        return t.execute();
    }

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "C:\\Users\\Сергей\\IdeaProjects\\MOIL3\\src\\Server\\rmi.policy\\");
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        String name = "rmi://localhost/Compute1";
        try {
            LocateRegistry.getRegistry(1045);
            Compute engine = new ServerEngine();
            Naming.rebind(name, engine);
            System.out.println("ComputeEngine1 bound");
        } catch(Exception e) {
            System.err.println("ComputeEngine1 exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
