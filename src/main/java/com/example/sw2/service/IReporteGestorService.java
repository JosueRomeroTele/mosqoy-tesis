package com.example.sw2.service;

import com.example.sw2.entity.Reportes;

import java.io.ByteArrayInputStream;

public interface IReporteGestorService {

    //ByteArrayInputStream SedeOrClienteXxAnual_TrimesterOrMonth(int mes, int trimestre,int anho, int orderBy, int type) throws Exception;

    ByteArrayInputStream generarReporte(Reportes reportes) throws Exception;

}
