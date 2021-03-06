USE [paintSoft]
GO
/****** Object:  StoredProcedure [dbo].[spd_S_Factura_detalle]    Script Date: 07/06/2017 07:56:51 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spd_S_Factura_detalle]
	
	@pmtId_encabezado int,
	@pmtId_producto int,
	@pmtId_unidad int,
	@pmtCantidad int,
	@pmtPrecio_unidad int,
	@pmtPrecio_venta int,
	@pmtMargen_ganancia float,
	@pmtDescuento float,
	@pmtIva float,
	@pmtEstado bit
	 
AS
BEGIN
	
	SET NOCOUNT ON;

	declare @Existe_Item int;
	declare @Id_encabezado int;
	select @Id_encabezado = max(Id) from tblFacturaEncabezadoCompra;
	select @Existe_Item = count(*) from tblFacturaDetalle 	where Id_encabezado = @pmtId_encabezado and Id_producto = @pmtId_producto and Id_unidad = @pmtId_unidad;
	
	If( @Existe_Item = 0 )
		BEGIN/* No existe el item en detalle de factura  */

			INSERT INTO dbo.tblFacturaDetalle (Id_encabezado, Id_producto,Id_unidad,Cantidad,Precio_unidad,Precio_venta,Margen_ganancia,Descuento,Iva,Estado)
			VALUES (@Id_encabezado, @pmtId_producto,@pmtId_unidad,@pmtCantidad,@pmtPrecio_unidad,@pmtPrecio_venta,@pmtMargen_ganancia,@pmtDescuento,@pmtIva,@pmtEstado);

		END
	ELSE
		BEGIN/* Actualiza detalle de factura */


			UPDATE dbo.tblFacturaDetalle 
			SET Cantidad = @pmtCantidad,
				Precio_unidad = @pmtPrecio_unidad,
				Precio_venta = @pmtPrecio_venta,
				Margen_ganancia = @pmtMargen_ganancia,
				Descuento = @pmtDescuento,
				Iva = @pmtIva,
				Estado = @pmtEstado
				where Id_encabezado = @Id_encabezado and
				      Id_producto = @pmtId_producto and
				      Id_unidad = @pmtId_unidad
		END
	


		/* Almacenar en inventario */
		DECLARE @pmtCompra int;
		set @pmtCompra = @pmtCantidad;
		DECLARE @Existe int;   
		SELECT  @Existe = count(*) from tblInventario where Id_producto = @pmtId_producto and Id_unidad_medida = @pmtId_unidad;

		IF(@Existe = 0)
			BEGIN /* No existe el producto en inventario, se agrega --> Estado 0 */

					INSERT INTO dbo.tblInventario 
						  (Id_producto,Compra,Venta,Stock,Perdida,Estado,Id_unidad_medida,Precio_venta,Iva,Fecha,Fecha_modificacion)
					VALUES(@pmtId_producto,@pmtCompra,0,@pmtCompra,0,0, @pmtId_unidad,@pmtPrecio_venta,@pmtIva, CONVERT (date, SYSDATETIME()), CONVERT (date, SYSDATETIME()));

			END
		ELSE
			BEGIN/* Tiene id_producto y id_medida debo seleccionar el maximo id: */

			DECLARE @maxId int, @Stock int, @Estado bit;

			/* Selecciona "Id" del último registro */
			SELECT @maxId = max(Id) from tblInventario where Id_producto = @pmtId_producto and Id_unidad_medida = @pmtId_unidad;
			/* Selecciona "Estado" del último registro de ese producto */
			SELECT @Estado = Estado from tblInventario where Id = @maxId;


				IF (@Estado = 0 )
					BEGIN/* Producto no ha pasado por inventario aún 
							 Puedo actualizar compra sumar cantidad existente con entrante*/

						/* Aumento el la cantidad comprada */
						SELECT @pmtCompra = Compra from tblInventario where Id = @maxId;
						set @pmtCompra = @pmtCompra + @pmtCantidad;

						/* Aumento el valor de Stock */
						select @Stock = Stock from tblInventario where Id = @maxId;
						set @Stock = @Stock + @pmtCantidad;


						UPDATE tblInventario 
						SET Compra = @pmtCompra,
							Stock = @Stock,
							Precio_venta = @pmtPrecio_venta,
							Iva = @pmtIva,
							Fecha_modificacion =  CONVERT (date, SYSDATETIME())
						WHERE Id = @maxId;
					END
				ELSE 
					BEGIN /* Estado --> 1 --- Ya se le hizo inventario a este producto */
							SELECT @Stock = Stock from tblInventario where Id = @maxId;
							SET @Stock = @Stock + @pmtCompra;

					INSERT INTO dbo.tblInventario (Id_producto,Compra,Venta,Stock,Perdida,Estado,Id_unidad_medida,Precio_venta,Iva,Fecha,Fecha_modificacion)
					VALUES(@pmtId_producto,@pmtCompra,0,@Stock,0,0, @pmtId_unidad,@pmtPrecio_venta,@pmtIva, CONVERT (date, SYSDATETIME()), CONVERT (date, SYSDATETIME()))
				
					END
			 END

		select max(Id) as Id from tblFacturaDetalle;

END