package com.example.sw2.service;

import com.example.sw2.entity.Reportes;
import com.example.sw2.entity.Usuarios;

import java.io.ByteArrayInputStream;

public interface IReporteSedeService {

    ByteArrayInputStream generarReporte(Reportes reportes, int idusuario) throws Exception;

}
