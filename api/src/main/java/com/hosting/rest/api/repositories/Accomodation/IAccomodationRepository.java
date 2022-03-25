package com.hosting.rest.api.repositories.Accomodation;

import com.hosting.rest.api.models.Accomodation.AccomodationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Francisco Coya · https://github.com/FranciscoCoya
 * @version v1.0.0
 * @description
 **/
public interface IAccomodationRepository extends JpaRepository<AccomodationModel, String>,
        PagingAndSortingRepository<AccomodationModel, String> {
}