package com.example.sw2.service;

import com.example.sw2.controller.ExcelHelper;
import com.example.sw2.entity.Inventario;
import com.example.sw2.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    @Autowired
    InventarioRepository inventarioRepository;

        public void save(MultipartFile file){
            try{
                List<Inventario> inventarios = ExcelHelper.excelToInventario(file.getInputStream());
                inventarioRepository.saveAll(inventarios);
            }catch(IOException e) {
                throw new RuntimeException("fail to store excel data : " + e.getMessage() + " --->" + e.getCause().getMessage());
            }
        }
    public List<Inventario> getAllInventario(){
        return inventarioRepository.findAll();
    }
}
