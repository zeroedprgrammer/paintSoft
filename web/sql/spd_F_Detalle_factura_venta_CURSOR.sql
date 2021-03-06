USE [paintSoft]
GO
/****** Object:  StoredProcedure [dbo].[spd_F_Detalle_factura_venta]    Script Date: 31/03/2017 11:55:54 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spd_F_Detalle_factura_venta] 
	/* Parámetros */
	@pmtId_encabezado_factura int
AS
BEGIN
	SET NOCOUNT ON;
			/*
				S1: Cuando no hay fraccion y ---> 
				S2: Cuando hay fracción 		
							
	select distinct fd.Id,fd.Id_encabezado_venta,fd.Id_unidad,fd.Tipo,fd.Id_fraccion,fd.Cantidad,
		   fd.Precio_unidad,fd.Descuento,fd.Iva,fd.Estado,fd.Id_producto,fd.Id_formula,
		   p.Descripcion,p.Codigo,um.Valor as Unidad
	from tblFacturaDetalleVenta as fd, tblProducto as p, tblUnidadMedida as um
	where fd.Id_producto = p.Id and fd.Id_unidad = um.Id and fd.Id_encabezado_venta = @pmtId_encabezado_factura;
	*/
					DECLARE @Tipo int, @Id int;  

					DECLARE FDetalle_venta_cursor CURSOR FOR
					  
							SELECT Id,Tipo FROM tblFacturaDetalleVenta
							WHERE Id_encabezado_venta = 2845; --@pmtId_encabezado_factura;

					OPEN FDetalle_venta_cursor;

					FETCH NEXT FROM FDetalle_venta_cursor   
					INTO @Id,@Tipo  
 
					WHILE @@FETCH_STATUS = 0  
					   BEGIN  

								if(@Tipo=0 or @Tipo=1)
									begin
										select distinct fd.Id,fd.Id_encabezado_venta,fd.Id_unidad,fd.Tipo,
														fd.Id_fraccion,fd.Cantidad,fd.Precio_unidad,fd.Descuento,
														fd.Iva,fd.Estado,fd.Id_producto,fd.Id_formula,p.Descripcion,
														p.Codigo,um.Valor as Unidad
										from tblFacturaDetalleVenta as fd, tblProducto as p, tblUnidadMedida as um
										where fd.Id = @Id and fd.Id_producto = p.Id and fd.Id_unidad = um.Id;

									end
								else if(@Tipo=3)
									begin
										
										select distinct fd.Id,fd.Id_encabezado_venta,0 as Id_unidad,fd.Tipo,
														 0 as Id_fraccion,fd.Cantidad,fd.Precio_unidad,fd.Descuento,
														fd.Iva,fd.Estado,0 as Id_producto,fd.Id_formula,
														 fm.Descripcion as Descripcion,
														'FORMULA'Codigo,'unidad' as Unidad
										from tblFacturaDetalleVenta as fd,  tblFormula as fm
										where fd.Id = @Id and fm.Id = fd.Id_formula;

									end
		
						   FETCH NEXT FROM FDetalle_venta_cursor  
						   INTO @Id,@Tipo;
					   END;  

					CLOSE FDetalle_venta_cursor;  
					DEALLOCATE FDetalle_venta_cursor;  
					  

END
