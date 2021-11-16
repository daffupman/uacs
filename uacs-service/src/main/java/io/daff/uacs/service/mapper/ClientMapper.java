package io.daff.uacs.service.mapper;

import io.daff.uacs.service.entity.po.Client;

import java.util.List;

public interface ClientMapper {

    List<Client> selectAll();
}