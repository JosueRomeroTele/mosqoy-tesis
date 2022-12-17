package com.example.sw2.Dao;


import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.entity.Inventario;
import com.example.sw2.entity.StorageServiceResponse;
import com.example.sw2.entity.Usuarios;
import com.example.sw2.entity.Ventas;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Component
public class StorageServiceDao {

    private final String USER_PATH = "saveProfile";
    private final String INVENTORY_PATH = "saveInventory" ;
    private final String DOCUMENT_PATH = "saveDocument" ;

    private final String BASE_URL = "https://storage-service.mosqoy-sw2.dns-cloud.net/";
    private final String API_KEY = "80e50707-27f2-481a-96d5-23e61f4cd29c-p4ssw0rd-r4nd0m";
    //private String fileLocation = "C:/FotosProyecto/";
    private String fileLocation = "/home/ec2-user/FotosProyecto/";
    public StorageServiceResponse store(Usuarios u, MultipartFile multipartFile) throws IOException {
        if(multipartFile!=null){
            String name = Integer.toString(u.getIdusuarios()*CustomConstants.BIGNUMBER).hashCode()+Integer.toString(u.getIdusuarios());
            StorageServiceResponse response = new RestTemplate().postForObject(BASE_URL+USER_PATH,
                    prepareEntity(multipartFile,name),StorageServiceResponse.class);
            if (response==null){
                return new StorageServiceResponse(){{
                    setStatus("error");
                    setMsg("Error en el guardado de archivo");}};
            }
            else if (response.getStatus().equals("success")){
                u.setFoto(response.getUrl());
            }
            return response;
        }
        return null;
    }

    public HashMap<String,String> store(Inventario inventario, MultipartFile multipartFile) throws IOException {
        HashMap<String,String> map = new HashMap<>();
        if(multipartFile!=null){
            String name = inventario.getCodigoinventario().toLowerCase().trim();

            //Nombre aleatorio del file
            int numeroAleatorio = (int) (Math.random()*999999999+1);
            String aleatorio= String.valueOf(numeroAleatorio);
            String fileName=aleatorio+".jpg";
            //String fileName = u.getIdusuarios() + ".jpg";

                try(InputStream inputStream = multipartFile.getInputStream()){
                    Path filePath = Paths.get(fileLocation);
                    Files.copy(inputStream,filePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    map.put("estado","exito");
                    map.put("fileName",fileName);
                    inventario.setFoto(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            return map;
        }
        map.put("estado","vacio");
        map.put("fileName","defaultprofile.png");
        return null;
    }

    public HashMap<String,String> storeVenta(Ventas v, MultipartFile multipartFile) {
        HashMap<String,String> map = new HashMap<>();
        if(multipartFile!=null){
            int numeroAleatorio = (int) (Math.random()*999999999+1);
            String aleatorio= String.valueOf(numeroAleatorio);
            String fileName=aleatorio+".jpg";
            //String fileName = u.getIdusuarios() + ".jpg";

            try(InputStream inputStream = multipartFile.getInputStream()){
                Path filePath = Paths.get(fileLocation);
                Files.copy(inputStream,filePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                map.put("estado","exito");
                map.put("fileName",fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        return map;
        }
        return null;
    }

    private HttpEntity<MultiValueMap<String,Object>> prepareEntity(MultipartFile multipartFile, String name) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource contentsAsResource = new ByteArrayResource(multipartFile.getBytes()){
            @Override
            public String getFilename(){ return multipartFile.getOriginalFilename(); }
        };
        body.add("key", API_KEY);
        body.add("file", contentsAsResource);
        body.add("name", name);
        return new HttpEntity<>(body,headers);
    }


    public HashMap<String, String> storelocalUsuario(Usuarios u,MultipartFile file){
        HashMap<String,String> map = new HashMap<>();
        int numeroAleatorio = (int) (Math.random()*999999999+1);
        String aleatorio= String.valueOf(numeroAleatorio);
        String fileName=aleatorio+".jpg";
        //String fileName = u.getIdusuarios() + ".jpg";
        if (file==null){
            map.put("estado","default");
            map.put("fileName","defaultprofile.png");
        }else{
            try(InputStream inputStream = file.getInputStream()){
                Path filePath = Paths.get(fileLocation);
                Files.copy(inputStream,filePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                map.put("estado","exito");
                map.put("fileName",fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }


}
