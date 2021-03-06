USE [paintSoft]
GO
/****** Object:  StoredProcedure [dbo].[spd_S_Abono]    Script Date: 06/05/2017 05:13:13 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
Create PROCEDURE [dbo].[spd_S_Abono]
	/* Parámtros */
		@pmtId_factura int,
		@pmtValor_abono int
AS
BEGIN
	SET NOCOUNT ON;

		declare @Saldo float;

		select @Saldo = Saldo from tblFacturaEncabezadoVenta where Id = @pmtId_factura;

		set @Saldo = @Saldo - @pmtValor_abono;

		if(@Saldo = 0)
			begin /* Se procede a cerrar la factura */
					update tblFacturaEncabezadoVenta 
					set Saldo = @Saldo, Estado = 1 
					where Id = @pmtId_factura; 
			end
		else
			begin
					update tblFacturaEncabezadoVenta 
					set Saldo = @Saldo
					where Id = @pmtId_factura;
			end


			/* Setter for tblAbono */
				
				declare @Numero_abono varchar(50);

				select @Numero_abono =  Numero from tblFacturaEncabezadoVenta where Id=@pmtId_factura;

				select @Numero_abono = @Numero_abono+'-A'+convert(varchar(50),((select count(Id) from tblAbono_venta where Id_encabezado_venta=@pmtId_factura)+1))
				
				insert into tblAbono_venta 
					(Id_encabezado_venta,Numero,Valor,Fecha)values
					(@pmtId_factura,@Numero_abono,@pmtValor_abono,CONVERT (date, SYSDATETIME()))


			/* Falta agregar la fila de la tabla abono */
			select @Saldo as Saldo;
END

/* tblAbono_venta

	Id ---> int
	Id_encabezado_venta ---> int
	Numero	--->	varchar			---> mmddaa001-A1
	Valor	--->	int
	Fecha	--->	date
*/