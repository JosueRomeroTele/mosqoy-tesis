package com.example.sw2.constantes;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;


public final class CustomConstants {

    public final static String AWS_BUCKET_NAME = "mosqoy-sw2-bucket";
    public final static String INVENTARIO = "inventario";
    public final static String PERFIL = "perfil";
    public final static int BIGNUMBER = 1749183;
    public  final static int SEDE_PRINCIPAL = 1;
    // Poner el email para que las alertas lleguen
    public final static String[] MANAGER_EMAIL = {"josue19979mosqoy@gmail.com"};

    private static final LinkedHashMap<String, String> lineas = new LinkedHashMap<>();
    private static final HashMap<Integer, String> meses = new HashMap<>();
    private static final HashMap<Integer, String> tiposAdquisicion = new HashMap<>();
    private static final HashMap<Integer, String> tiposDocumento = new HashMap<>();
    private static final HashMap<Integer, String> estadoAsignacion = new HashMap<>();
    private static final LinkedHashMap<String, String> tamanhos = new LinkedHashMap<>();

    public static final HashMap<Integer, String> MediosDePago = new HashMap<>();

    private static final HashMap<Integer,String> notificationState = new HashMap<>();
    private static final HashMap<Integer,String> notificationType = new HashMap<>();

    private static final HashMap<Integer, String> trimestre = new HashMap<>();

    static {
        lineas.put("T", "Tradicional");
        lineas.put("M", "Mosqoy");
        lineas.put("F", "Fibras");

        meses.put(1,"JAN");
        meses.put(2,"FEB");
        meses.put(3,"MAR");
        meses.put(4,"APR");
        meses.put(5,"MAY");
        meses.put(6,"JUN");
        meses.put(7,"JUL");
        meses.put(8,"AUG");
        meses.put(9,"SEP");
        meses.put(10,"OCT");
        meses.put(11,"NOV");
        meses.put(12,"DEC");

        tamanhos.put("OS", "One-Size");
        tamanhos.put("S", "Small");
        tamanhos.put("M", "Medium");
        tamanhos.put("L", "Large");

        tiposAdquisicion.put(0, "Comprado");
        tiposAdquisicion.put(1, "Consignado");

        tiposDocumento.put(1, "Factura");
        tiposDocumento.put(2, "Boleta");
        tiposDocumento.put(3, "Recibo");
        tiposDocumento.put(4, "Transacción");

        estadoAsignacion.put(1, "Enviado a Sede");
        estadoAsignacion.put(2, "Recibido por Sede");
        estadoAsignacion.put(3, "Recibido con Problemas");
        estadoAsignacion.put(4, "Devuelto por Sede");
        estadoAsignacion.put(5,"Devuelto al Artesano");

        notificationState.put(1, "Nuevo");
        notificationState.put(2, "Leído");
        notificationState.put(3, "Archivado");

        notificationType.put(1, "aviso");
        notificationType.put(2, "alerta");
        notificationType.put(3, "problema");
        notificationType.put(4, "devolucion");
        notificationType.put(5, "otro");

        MediosDePago.put(1,"Efectivo");
        MediosDePago.put(2,"Depósito/Transferencia");
        MediosDePago.put(3,"Tarjeta de crédito");

        trimestre.put(1,"EN-FEB-MAR");
        trimestre.put(2,"ABR-MAY-JUN");
        trimestre.put(3,"JUL-AGO-SET");
        trimestre.put(4,"OCT-NOV-DIC");
    }

    public final static int N_TYPE_NOTICE = 1;
    public final static int N_TYPE_ALERT = 2;
    public final static int N_TYPE_PROBLEM = 3;
    public final static int N_TYPE_RETURN = 4;
    public final static int N_TYPE_OTHER = 5;

    public final static int NOTF_STATE_NEW = 1;
    public final static int NOTF_STATE_READ = 2;
    public final static int NOTF_STATE_ARCHIVED = 3;



    public final static int ESTADO_ENVIADO_A_SEDE=1;
    public final static int ESTADO_RECIBIDO_POR_SEDE=2;
    public final static int ESTADO_RECIBIDO_CON_PROBLEMAS=3;
    public final static int ESTADO_DEVUELTO_POR_SEDE=4;
    public final static int ESTADO_DEVUELTO_AL_ARTESANO = 5;


    public static HashMap<Integer, String> getEstadoAsignacion() {
        return estadoAsignacion;
    }

    public static LinkedHashMap<String, String> getLineas() {
        return lineas;
    }

    public static HashMap<Integer, String> getMeses() {
        return meses;
    }

    public static LinkedHashMap<String, String> getTamanhos() {
        return tamanhos;
    }

    public static HashMap<Integer, String> getTiposAdquisicion() {
        return tiposAdquisicion;
    }

    public static HashMap<Integer, String> getTiposDocumento() {
        return tiposDocumento;
    }

    public static HashMap<Integer, String> getTrimestre() {
        return trimestre;
    }

    public static HashMap<Integer, String> getMediosDePago() {
        return MediosDePago;
    }
}
