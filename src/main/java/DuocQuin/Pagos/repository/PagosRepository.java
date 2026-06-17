package DuocQuin.Pagos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DuocQuin.Pagos.model.PagosModel;

@Repository
public interface PagosRepository extends JpaRepository <PagosModel, Long>{

    List<PagosModel> findByIdUsuario(Long idUsuario);

}
