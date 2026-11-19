package co.com.crediya.autenticacion.jpa;

import co.com.crediya.autenticacion.jpa.entity.UsuarioEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface JPARepository extends CrudRepository<UsuarioEntity, Long>, QueryByExampleExecutor<UsuarioEntity> {
    boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);

    Optional<UsuarioEntity> findByCorreoElectronicoIgnoreCase(String correoElectronico);

    boolean existsByDocumento(String documento);
}