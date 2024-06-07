use [master]
go

create database [EMPLEADOS]
go

USE [EMPLEADOS]
GO
/****** Object:  Table [dbo].[Usuarios]    Script Date: 07/06/2024 14:39:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Usuarios]') AND type in (N'U'))
DROP TABLE [dbo].[Usuarios]
GO
/****** Object:  Table [dbo].[Familiares]    Script Date: 07/06/2024 14:39:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Familiares]') AND type in (N'U'))
DROP TABLE [dbo].[Familiares]
GO
/****** Object:  Table [dbo].[Empleados]    Script Date: 07/06/2024 14:39:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Empleados]') AND type in (N'U'))
DROP TABLE [dbo].[Empleados]
GO
/****** Object:  Table [dbo].[Empleados]    Script Date: 07/06/2024 14:39:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Empleados](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[identificacion] [nvarchar](20) NOT NULL,
	[nombre] [nvarchar](100) NOT NULL,
	[correo] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_Empleados] PRIMARY KEY CLUSTERED 
(
	[id] ASC,
	[identificacion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Familiares]    Script Date: 07/06/2024 14:39:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Familiares](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[identificacion] [nvarchar](20) NOT NULL,
	[detalle] [nvarchar](200) NOT NULL,
 CONSTRAINT [PK_Familiares] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Usuarios]    Script Date: 07/06/2024 14:39:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Usuarios](
	[usuario] [nvarchar](20) NOT NULL,
	[contrasena] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_Usuarios] PRIMARY KEY CLUSTERED 
(
	[usuario] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[Usuarios] ([usuario], [contrasena]) VALUES (N'admin', N'123456')
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [C_Keys]    Script Date: 07/06/2024 14:39:37 ******/
ALTER TABLE [dbo].[Empleados] ADD  CONSTRAINT [C_Keys] UNIQUE NONCLUSTERED 
(
	[identificacion] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Familiares]  WITH CHECK ADD  CONSTRAINT [FK_Empleados_Identificacion] FOREIGN KEY([identificacion])
REFERENCES [dbo].[Empleados] ([identificacion])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Familiares] CHECK CONSTRAINT [FK_Empleados_Identificacion]
GO
