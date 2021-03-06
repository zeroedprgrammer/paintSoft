/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Conexion;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Zero
 */
public class InventarioDAO {


    public InventarioDAO(){}

    /* Buscar cantidades de un producto */
    public String getStockProducto(int Control,int Id_producto, int Id_medida){

       Conexion sql = new Conexion();
       String listStockProducto = "[";



        try {

                PreparedStatement cstmt = sql.getConexion().prepareStatement("{call dbo.spd_F_Stock_producto(?,?,?)}",
                ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

                  /* Parametros IN */
                 cstmt.setInt(1,Control);
                 cstmt.setInt(2,Id_producto);
                 cstmt.setInt(3,Id_medida);

                 ResultSet  r = cstmt.executeQuery();/* Ejecuta procedimiento almacenado */


                 while (r.next()) {

                        listStockProducto = listStockProducto + "{";
                        /* Parámetros recibidos */
                        String stock = String.valueOf(r.getInt("Stock"));
                        String id_unidad_medida = String.valueOf(r.getInt("Id_unidad_medida"));
                        String unidad_medida = r.getString("Unidad_medida");
                        String precio_venta = String.valueOf(r.getInt("Precio_venta"));
                        String precio_compra = String.valueOf(r.getInt("Precio_compra"));
                        String iva = String.valueOf(r.getFloat("Iva"));

                        listStockProducto = listStockProducto +  "\"Stock\":"+stock+
                                ",\"Id\":"+id_unidad_medida+
                                ",\"Id_medida\":"+Id_medida+
                                ",\"Precio_venta\":"+precio_venta+
                                ",\"Precio_compra\":"+precio_compra+
                                ",\"Iva\":"+iva+
                                ",\"Unidad_medida\":\""+unidad_medida+"\"}";

                        if(r.isLast()==false){
                                 listStockProducto = listStockProducto + ",";
                        }
                    }

                        listStockProducto = listStockProducto + "]";
                        cstmt.close();
                        sql.getConexion().close();


        } catch (SQLException e) {
             System.out.println(e.getMessage());
        }
        return listStockProducto;
    }



    public String doInventario(int pmtId_producto,int pmtId_unidad_medida,int pmtCantidad,int pmtPrecio_venta,int pmtPrecio_compra,float pmtIva){

            Conexion sql= new Conexion();
            String Id = new String();

            try
            {

                PreparedStatement cstmt = sql.getConexion().prepareStatement("{call dbo.spd_S_Inventario(?,?,?,?,?,?)}",
                ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

                System.out.println("Almacenando...");

                 /* Parametros IN */
                 cstmt.setInt(1, pmtId_producto);
                 cstmt.setInt(2, pmtId_unidad_medida);
                 cstmt.setInt(3, pmtCantidad);
                 cstmt.setInt(4, pmtPrecio_venta);
                 cstmt.setInt(5, pmtPrecio_compra);
                 cstmt.setFloat(6, pmtIva);

                 ResultSet  r = cstmt.executeQuery();

                 while (r.next()) {
                     Id = r.getString("Id");
                 }
                 cstmt.close();
                 sql.getConexion().close();

            }catch (SQLException e) {
                 System.out.println(e.getMessage());
            }
       return Id;

    }

    public float producto_Empezado(int pmtId_producto,int pmtId_unidad_medida){

        /* Debe regresar una cantidad para compararla con el valor requerido */
          Conexion sql= new Conexion();
          float Restante = 0;

            try
            {

                PreparedStatement cstmt = sql.getConexion().prepareStatement("{call dbo.spd_F_producto_Empezado(?,?)}",
                ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

                 /* Parametros IN */
                 cstmt.setInt(1, pmtId_producto);
                 cstmt.setInt(2, pmtId_unidad_medida);

                 ResultSet  r = cstmt.executeQuery();

                 while (r.next()) {
                        Restante = r.getFloat("Restante");
                 }
                 cstmt.close();
                 sql.getConexion().close();

            }catch (SQLException e) {
                 System.out.println(e.getMessage());
            }


        return Restante;
    }


    public String Total(){

        String list = new String();



        Conexion sql= new Conexion();


            try
            {

                PreparedStatement cstmt = sql.getConexion().prepareStatement("SELECT 	CASE WHEN Precio_compra IS NULL THEN 0 ELSE Precio_compra END as Precio_compra, Precio_venta, Stock From tblInventario I where I.stock>0 and I.Id IN (SELECT MAX(I2.Id) FROM tblInventario I2 WHERE I2.Id_producto=I.Id_producto and I2.Id_unidad_medida=I.Id_unidad_medida )  order by I.Id_producto asc",
                ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

                 /* Parametros IN */

                 ResultSet  r = cstmt.executeQuery();
                  list = "[";
                 while (r.next()) {

                      list = list +  "{\"Cantidad\":"+String.valueOf(r.getInt("Stock"))+
                                ",\"Precio_venta\":"+String.valueOf(r.getInt("Precio_venta"))+
                                ",\"Precio_compra\":"+String.valueOf(r.getInt("Precio_compra"))+"}";

                      if(r.isLast()==false){
                         list = list + ",";
                     }

                 }

                    list = list + "]";

                 cstmt.close();
                 sql.getConexion().close();

            }catch (SQLException e) {
                 System.out.println(e.getMessage());
            }



        return list;
    }

    public String getFecha_ultimo_inventario(int pmtId_producto,int pmtId_unidad_medida){

        String json_ultimo_inventario = "[";
        Conexion sql = new Conexion();

        try{
                PreparedStatement pstmt = sql.getConexion().prepareStatement("{ call spd_F_fecha_ultimo_inventario(?,?)}",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                pstmt.setInt(1, pmtId_producto);
                pstmt.setInt(2, pmtId_unidad_medida);

                ResultSet r = pstmt.executeQuery();

                while(r.next()){

                       json_ultimo_inventario = json_ultimo_inventario + "{";
                        /* Parámetros recibidos */
                        String Fecha_unidad = r.getString("Fecha_unidad");
                        String Fecha_empezado = r.getString("Fecha_empezado");
                        String Restante = r.getString("Restante");

                        json_ultimo_inventario = json_ultimo_inventario +
                                "\"Fecha_unidad\":\""+Fecha_unidad+
                                "\",\"Fecha_empezado\":\""+Fecha_empezado+
                                "\",\"Restante\":\""+Restante+"\"}";

                        if(r.isLast()==false){
                                 json_ultimo_inventario = json_ultimo_inventario + ",";
                        }
                }

                        json_ultimo_inventario = json_ultimo_inventario + "]";
                        pstmt.close();
                        sql.getConexion().close();


        }catch(SQLException e){System.out.println(e.getMessage());}

        return json_ultimo_inventario;
    }

    public String setCantidad_producto_sin_Estado(int pmtId_producto,int pmtId_unidad_medida, int pmtCantidad){
        String rs = new String();

        Conexion sql = new Conexion();

        try{
                PreparedStatement pstmt = sql.getConexion().prepareStatement("{ call spd_S_Cantidad_producto_sin_Estado(?,?,?)}",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                pstmt.setInt(1, pmtId_producto);
                pstmt.setInt(2, pmtId_unidad_medida);
                pstmt.setInt(3, pmtCantidad);

                ResultSet r = pstmt.executeQuery();

                while(r.next()){
                    rs = r.getString("Id");
                }


        }catch(SQLException e){System.out.println(e.getMessage());}



        return rs;
    }

      public String setCantidad_producto_empezado(int pmtId_producto,int pmtId_unidad_medida, float pmtCantidad){
        String rs = new String();

        Conexion sql = new Conexion();

        try{
                PreparedStatement pstmt = sql.getConexion().prepareStatement("{ call spd_S_Cantidad_producto_empezado(?,?,?)}",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                pstmt.setInt(1, pmtId_producto);
                pstmt.setInt(2, pmtId_unidad_medida);
                pstmt.setFloat(3, pmtCantidad);

                ResultSet r = pstmt.executeQuery();

                while(r.next()){
                    rs = r.getString("Id");
                }


        }catch(SQLException e){System.out.println(e.getMessage());}



        return rs;
    }

      public String setPrecio_venta_gramo(int pmtId_producto,int pmtId_unidad_medida, int pmtPrecio_venta, int pmtPrecio_gramo){

          String rs = new String();

           Conexion sql = new Conexion();

        try{
                PreparedStatement pstmt = sql.getConexion().prepareStatement("{ call spd_U_Precio_venta_gramo(?,?,?,?)}",
                                          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                pstmt.setInt(1, pmtId_producto);
                pstmt.setInt(2, pmtId_unidad_medida);
                pstmt.setInt(3, pmtPrecio_venta);
                pstmt.setInt(4, pmtPrecio_gramo);

                ResultSet r = pstmt.executeQuery();

                while(r.next()){
                    rs = r.getString("Id");
                }


        }catch(SQLException e){System.out.println(e.getMessage());}



          return rs;
      }


   public String Informe_productos_inventario(){

              Conexion sql = new Conexion();
              JSONArray inventario = new JSONArray();/* Lista de inventario */

              try{
                           PreparedStatement cstmt = sql.getConexion().prepareStatement("{call dbo.spd_Informe_productos_inventario()}",
                           ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                           ResultSet  r = cstmt.executeQuery();

                         while(r.next()){
                              /* Recibe productos  */
                                   JSONObject producto = new JSONObject();
                                              producto.put("Id",r.getInt("Id"));
                                              producto.put("Id_producto",r.getInt("Id_producto"));
                                              producto.put("Descripcion",r.getString("Descripcion"));
                                              producto.put("Presentacion",r.getString("Presentacion"));
                                              producto.put("Stock",r.getInt("Stock"));
                                              producto.put("Estado",r.getInt("Estado"));
                                              producto.put("Id_unidad_medida",r.getString("Id_unidad_medida"));
                                              producto.put("Fecha",r.getString("Fecha"));
                                              producto.put("Fecha_modificacion",r.getString("Fecha_modificacion"));
                                              producto.put("Precio_venta",r.getInt("Precio_venta"));
                                              producto.put("Precio_compra",r.getInt("Precio_compra"));

                                  inventario.add(inventario.size(),producto);
                         }/* end while */

                         /* Close connection */
                              r.close();
                              cstmt.close();
                              sql.getConexion().close();

              }catch(SQLException e){System.out.println(e.getMessage());}

           return inventario.toJSONString();

        }/* ## spd_Informe_productos_inventario ##*/

}
