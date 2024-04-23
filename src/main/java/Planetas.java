import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Planetas {
    private static java.sql.Connection con;
    private static int currentScreen = 0;

    public static void main(String[] args) throws SQLException {

        int opcion;
        String host = "jdbc:sqlite:src/main/resources/planetas.sqlite";
        con = java.sql.DriverManager.getConnection(host);
        while (true) {
            imprimirMenu();
            opcion = obtenerOpcion();
            if (opcion == 0) break;
            if (currentScreen == 0) {
                switch (opcion) {
                    case 1:
                        // Todos los Sistemas
                        todosSistemas();
                        break;
                    case 2:
                        // Todos los Planetas
                        todosPlanetas();
                        break;
                    case 3:
                        //Listar Planetas por Nombre y Sistema
                        listarPlanetas();
                        break;
                    case 4:
                        // Agregar Sistema
                        agregarSistema();
                        break;
                    case 5:
                        // Agregar Planeta
                        agregarPlaneta();
                        break;
                    case 6:
                        // Eliminar Sistema
                        todosSistemas();
                        eliminarSistema();
                        break;
                    case 7:
                        // Eliminar Planeta
                        todosPlanetas();
                        eliminarPlaneta();
                        break;
                    case 8:
                        //Modificar Sistema
                        modificarSistema();
                        break;
                }
            }
        }
    }
    private static int obtenerOpcion() {
        Scanner sc = new Scanner(System.in);
        int opcion = -1;
        try {
            opcion = Integer.parseInt(sc.next());
            if (currentScreen == 0 && opcion < 1 || opcion > 6) {
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Opción incorrecta");
        }
        return opcion;
    }

    private static void imprimirMenu() {
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("0 Salir | 1 Todos los Sistemas | 2 Todos los Planetas | 3 Listar Planetas por Nombre y Sistema | 4 Agregar Sistema | 5 Agregar Planeta | 6 Eliminar Sistema | 7 Eliminar Planeta | 8 Modificar Sistema");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void todosSistemas() throws SQLException {
        String query = "SELECT * FROM sistema";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println("Nombre: " + rs.getString("name") +", Id del sistema: "+rs.getInt("id"));
        }
    }

    private static void todosPlanetas() throws SQLException {
        String query = "SELECT * FROM planeta";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println("Id del planeta: "+rs.getInt("id")+", Nombre: " + rs.getString("name") + ", ID del Sistema: " + rs.getInt("id_sistema"));
        }
    }

    private static void listarPlanetas() throws SQLException {

        String query = "SELECT p.id, p.name AS nombre_planeta, g.name AS nombre_galaxia  FROM planeta p  INNER JOIN sistema g on p.id_sistema like g.id";

        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            int idPlaneta = rs.getInt(1);
            String nombrePlaneta = rs.getString(2);
            String nombreSistema = rs.getString(3);
            System.out.println("ID del Planeta: " + idPlaneta + ", Nombre: " + nombrePlaneta + ", Galaxia: " + nombreSistema);
        }
    }

    private static void agregarSistema() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el nombre del sistema: ");
        String nombre = sc.nextLine();

        String query = "INSERT INTO sistema (name) VALUES (?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, nombre);
        st.executeUpdate();

        System.out.println("Se ha agregado el sistema correctamente");
    }

    private static void agregarPlaneta() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el nombre del planeta: ");
        String nombre = sc.nextLine();
        System.out.println("Introduce el id del sistema: ");
        int id_sistema = Integer.parseInt(sc.nextLine());

        String query = "INSERT INTO planeta (name,id_sistema) VALUES (?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, nombre);
        st.setInt(2, id_sistema);

        st.executeUpdate();

        System.out.println("Se ha agregado el planeta correctamente.");
    }

    private static void eliminarSistema() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el ID del sistema que deseas eliminar: ");
        int sistemaId = Integer.parseInt(sc.nextLine());

        // Verificar si el sistema tiene planetas
        String query = "SELECT COUNT(*) AS conteo FROM planeta WHERE id_sistema = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, sistemaId);
        ResultSet rs = st.executeQuery();
        rs.next();

        int conteoPlanetas = rs.getInt("conteo");

        if (conteoPlanetas > 0) {
            System.out.println("No se puede eliminar el sistema. Contiene planetas.");
            return;
        }

        String query2 = "DELETE FROM sistema WHERE id = ?";
        PreparedStatement st2 = con.prepareStatement(query2);
        st2.setInt(1, sistemaId);
        st2.executeUpdate();

        System.out.println("Se ha eliminado el sistema correctamente.");
    }

    private static void eliminarPlaneta() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el ID del planeta que deseas eliminar:");
        int planetaId = Integer.parseInt(sc.nextLine());

        String query = "DELETE FROM planeta WHERE id = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, planetaId);
        st.executeUpdate();

        System.out.println("Se ha eliminado el planeta correctamente.");
    }

    private static void modificarSistema() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el ID del sistema que deseas modificar: ");
        int sistemaId = Integer.parseInt(sc.nextLine());

        System.out.println("Introduce el nuevo nombre del sistema: ");
        String nuevoNombre = sc.nextLine();

        String query = "UPDATE sistema SET name = ? WHERE id = ?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, nuevoNombre);
        st.setInt(2, sistemaId);
        int filasModificadas = st.executeUpdate();

        if (filasModificadas > 0) {
            System.out.println("Se ha modificado el nombre del sistema correctamente.");
        } else {
            System.out.println("No se encontró el sistema con el ID proporcionado.");
        }
    }

}
