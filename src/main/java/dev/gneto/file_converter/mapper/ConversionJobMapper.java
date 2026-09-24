package dev.gneto.file_converter.mapper;

import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.model.ConversionJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ConversionJobMapper {
    ConversionJobMapper INSTANCE = Mappers.getMapper( ConversionJobMapper.class );

    @Mapping(source = "publicId", target = "id")
    ConversionResponse conversionJobToConversionResponse(ConversionJob conversionJob);
}
