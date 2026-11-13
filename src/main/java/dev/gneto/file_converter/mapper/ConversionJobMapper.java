package dev.gneto.file_converter.mapper;

import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.model.ConversionJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConversionJobMapper {

    @Mapping(source = "publicId", target = "id")
    @Mapping(source = "originalFileName", target = "filename")
    ConversionResponse conversionJobToConversionResponse(ConversionJob conversionJob);
}
