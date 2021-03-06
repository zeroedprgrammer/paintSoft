/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dao.EstadisticaDAO;

/**
 *
 * @author Zero
 */
public class ctrlEstadistica extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

             int pmtAction = Integer.parseInt(request.getParameter("A"));
            EstadisticaDAO opEstadistica = new EstadisticaDAO();

            String pmtFecha_inicial;
            String pmtFecha_final;

            switch(pmtAction){

                        case 1:/* Gasto - Ganancia diaria  */
                                  String EstadisticaDAO = opEstadistica.getGasto_Ganancia_diaria();
                                  response.getWriter().write(EstadisticaDAO);
                                break;
                        case 2:/* getInforme_ventas */
                                  pmtFecha_inicial = request.getParameter("Fecha_inicial");
                                  pmtFecha_final =  request.getParameter("Fecha_final");
                                  response.getWriter().write(opEstadistica.getInforme_ventas(pmtFecha_inicial,pmtFecha_final));
                                break;
                        case 3:/* getInforme_facturas_credito */
                                  pmtFecha_inicial = request.getParameter("Fecha_inicial");
                                  pmtFecha_final =  request.getParameter("Fecha_final");
                                  response.getWriter().write(opEstadistica.getInforme_facturas_credito(pmtFecha_inicial,pmtFecha_final));
                                break;
                        case 4:/* getInforme_facturas_credito */
                                  response.getWriter().write(opEstadistica.getInforme_productos_agotados());
                                break;
                        default:
                            break;
            }


    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
