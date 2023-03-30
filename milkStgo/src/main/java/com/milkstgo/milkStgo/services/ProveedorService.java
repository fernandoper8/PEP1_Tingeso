package com.milkstgo.milkStgo.services;

import com.milkstgo.milkStgo.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService {

    @Autowired
    ProveedorRepository proveedorRepository;
}
