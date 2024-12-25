package edu.ntnu.idatt2106.project.sparesti.mappers.impl;

import edu.ntnu.idatt2106.project.sparesti.domain.dto.users.UserDto;
import edu.ntnu.idatt2106.project.sparesti.domain.entities.UserEntity;
import edu.ntnu.idatt2106.project.sparesti.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/** Mapper class that maps between UserDTOs and UserEntities. */
@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

  /** Used for dependency injection. */
  private final ModelMapper modelMapper;

  /**
   * Used for dependency injection of ModelMapper object.
   *
   * @param modelMapper The injected ModelMapper object.
   */
  public UserMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Maps an UserEntity-instance to a UserDto.
   *
   * @param userEntity Object to convert.
   * @return Mapped UserDto object.
   */
  @Override
  public UserDto mapTo(UserEntity userEntity) {
    return modelMapper.map(userEntity, UserDto.class);
  }

  /**
   * Maps UserDto-instance to a UserEntity.
   *
   * @param userDto Object to convert.
   * @return Mapped userEntity object.
   */
  @Override
  public UserEntity mapFrom(UserDto userDto) {
    return modelMapper.map(userDto, UserEntity.class);
  }
}
