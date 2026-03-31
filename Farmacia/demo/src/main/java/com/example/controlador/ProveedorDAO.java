package com.example.controlador;

import com.example.modelo.ProveedorVO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<ProveedorVO> listarProveedores(){

        List<ProveedorVO> lista = new ArrayList<>();

        String sql = "SELECT * FROM proveedores";

        try(Connection cn = ConectarBase.conectar();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){

                ProveedorVO p = new ProveedorVO();

                p.setId(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setEmpresa(rs.getString("empresa_laboratorio"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));

                lista.add(p);
            }

        }catch(Exception e){
            System.out.println("Error listar proveedores "+e);
        }

        return lista;
    }

    


    public List<ProveedorVO> buscarProveedor(String texto){

        List<ProveedorVO> lista = new ArrayList<>();

        String sql = "SELECT * FROM proveedores WHERE nombre LIKE ? OR empresa_laboratorio LIKE ?";

        try(Connection cn = ConectarBase.conectar();
            PreparedStatement ps = cn.prepareStatement(sql)){

            ps.setString(1,"%"+texto+"%");
            ps.setString(2,"%"+texto+"%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                ProveedorVO p = new ProveedorVO();

                p.setId(rs.getInt("id_proveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setEmpresa(rs.getString("empresa_laboratorio"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));

                lista.add(p);
            }

        }catch(Exception e){
            System.out.println("Error buscar proveedor "+e);
        }

        return lista;
    }


    public boolean agregarProveedor(ProveedorVO p){

        String sql = "INSERT INTO proveedores(nombre,empresa_laboratorio,telefono,email) VALUES(?,?,?,?)";

        try(Connection cn = ConectarBase.conectar();
            PreparedStatement ps = cn.prepareStatement(sql)){

            ps.setString(1,p.getNombre());
            ps.setString(2,p.getEmpresa());
            ps.setString(3,p.getTelefono());
            ps.setString(4,p.getEmail());

            ps.executeUpdate();

            return true;

        }catch(Exception e){

            System.out.println("Error agregar proveedor "+e);
            return false;
        }
    }


    public boolean editarProveedor(ProveedorVO p){

        String sql = "UPDATE proveedores SET nombre=?,empresa_laboratorio=?,telefono=?,email=? WHERE id_proveedor=?";

        try(Connection cn = ConectarBase.conectar();
            PreparedStatement ps = cn.prepareStatement(sql)){

            ps.setString(1,p.getNombre());
            ps.setString(2,p.getEmpresa());
            ps.setString(3,p.getTelefono());
            ps.setString(4,p.getEmail());
            ps.setInt(5,p.getId());

            ps.executeUpdate();

            return true;

        }catch(Exception e){

            System.out.println("Error editar proveedor "+e);
            return false;
        }
    }


    public boolean eliminarProveedor(int id){

        String sql = "DELETE FROM proveedores WHERE id_proveedor=?";

        try(Connection cn = ConectarBase.conectar();
            PreparedStatement ps = cn.prepareStatement(sql)){

            ps.setInt(1,id);

            ps.executeUpdate();

            return true;

        }catch(Exception e){

            System.out.println("Error eliminar proveedor "+e);
            return false;
        }
    }

    

}