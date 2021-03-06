USE [paintSoft]
GO
/****** Object:  StoredProcedure [dbo].[spd_S_Cliente]    Script Date: 02/06/2017 11:39:16 a.m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spd_S_Cliente]
	
	@pmtId Int,
	@pmtNombre Varchar(50),
	@pmtDocumento Varchar(50),
	@pmtTelefono Varchar(50),
	@pmtDireccion Varchar(50),
	@pmtCiudad Varchar(50),
	@pmtEmail Varchar(50)
	 
AS
BEGIN
	SET NOCOUNT ON;

			declare @Existe int;

			select @Existe = count(*) from tblCliente where Id = @pmtId;

			if(@Existe>0)
				begin /* Actualiza cliente */
						UPDATE tblCliente 
						set Nombre = @pmtNombre,
							Documento = @pmtDocumento,
							Telefono = @pmtTelefono,
							Direccion = @pmtDireccion,
							Ciudad = @pmtCiudad,
							Email = @pmtEmail
						WHERE Id = @pmtId;

				end
			else
				begin /* Inserta cliente */
					
						INSERT INTO dbo.tblCliente (Nombre, Documento,Telefono,Direccion,Ciudad,Email)
							VALUES (@pmtNombre, @pmtDocumento,@pmtTelefono,@pmtDireccion,@pmtCiudad,@pmtEmail);

						SELECT max(Id) from tblCliente;
				end


	

END