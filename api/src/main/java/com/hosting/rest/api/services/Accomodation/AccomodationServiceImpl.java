package com.hosting.rest.api.services.Accomodation;

import static com.hosting.rest.api.Utils.AppUtils.isBigDecimalValid;
import static com.hosting.rest.api.Utils.AppUtils.isDoubleValidAndPositive;
import static com.hosting.rest.api.Utils.AppUtils.isIntegerValidAndPositive;
import static com.hosting.rest.api.Utils.AppUtils.isNotNull;
import static com.hosting.rest.api.Utils.AppUtils.isStringNotBlank;
import static com.hosting.rest.api.Utils.AppUtils.isValidGeographicCoordinate;
import static com.hosting.rest.api.Utils.ServiceGlobalValidations.checkPageNumber;
import static com.hosting.rest.api.Utils.ServiceGlobalValidations.checkPageSize;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParam;
import static com.hosting.rest.api.Utils.ServiceParamValidator.validateParamNotFound;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.hosting.rest.api.models.Accomodation.AccomodationModel;
import com.hosting.rest.api.models.Accomodation.AccomodationImage.AccomodationImageModel;
import com.hosting.rest.api.repositories.Accomodation.IAccomodationRepository;
import com.hosting.rest.api.repositories.User.UserHost.IUserHostRepository;

/**
 * 
 * @author Francisco Coya
 * @version v1.0.3
 * @description Servicio que implementa las acciones relacionadas a los
 *              alojamientos.
 * 
 **/
@Service
public class AccomodationServiceImpl implements IAccomodationService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private IAccomodationRepository accomodationRepo;

	@Autowired
	private IUserHostRepository userRepo;

	/**
	 * Registro de un nuevo alojamiento dentro de la aplicaci??n.
	 * 
	 * @param accomodationModel
	 * 
	 * @return
	 */
	@Override
	public AccomodationModel addNewAccomodation(final AccomodationModel accomodationModel) {

		// Si el alojamiento es null o no se pasa el n??mero de registro o el
		// propietario.
		validateParam(
				isNotNull(accomodationModel) || isNotNull(accomodationModel.getRegisterNumber())
						|| isNotNull(accomodationModel.getIdUserHost()),
				"Los datos introducidos para el alojamiento no son v??lidos o falta alg??n dato.");

		// Comprobar si existe el alojamiento
		validateParam(!accomodationRepo.existsById(accomodationModel.getRegisterNumber()),
				"Ya se encuentra registrado un alojamiento con n??mero de registro ["
						+ accomodationModel.getRegisterNumber() + " ].");

		return accomodationRepo.save(accomodationModel);
	}

	/**
	 * Listado de todos los alojamientos registrados en la aplicaci??n.
	 * 
	 * @param pageNumber
	 * @param size
	 * 
	 * @return
	 */
	@Override
	public Page<AccomodationModel> findAllAccomodations(final Integer pageNumber, final Integer pageSize) {
		// Comprobar que el n??mero de p??gina y el tama??o de esta son v??lidos.
		checkPageNumber(pageNumber);
		checkPageSize(pageSize);

		return accomodationRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending()));
	}

	/**
	 * Obtenci??n del alojamiento con n??mero de registro <code>regNumber</code>.
	 * 
	 * @see #validateParam
	 * 
	 * @param regNumber
	 * 
	 * @return
	 */
	@Override
	public AccomodationModel getAccomodationById(final String regNumber) {
		// Validar n??mero de registro del alojamiento.
		validateParam(isStringNotBlank(regNumber), "El n??mero de registro est?? vac??o.");

		return accomodationRepo.findById(regNumber).orElse(null);
	}

	/**
	 * Borrado del alojamiento con n??mero de registro <code>regNumber</code>.
	 * 
	 * @see #validateParam
	 * 
	 * @param regNumber
	 * 
	 * @return
	 */
	@Override
	public String removeAccomodationById(final String regNumber) {
		// Validar n??mero de registro del alojamiento.
		validateParam(isStringNotBlank(regNumber), "El n??mero de registro pasado como par??metro est?? vac??o.");

		// Comprobar que existe el alojamiento.
		validateParamNotFound(accomodationRepo.existsById(regNumber),
				"El alojamiento con n??mero de registro [ " + regNumber + " ] no existe.");

		accomodationRepo.deleteById(regNumber);

		return "Alojamiento con n??mero de registro [ " + regNumber + " ] eliminado correctamente";
	}

	/**
	 * Listado de los alojamientos de la ciudad <code>cityToSearch</code>.
	 * 
	 * @param cityToSearch
	 * @param pageNumber
	 * @param size
	 * 
	 * @return
	 */
	@Override
	public Page<AccomodationModel> findByCity(final String cityToSearch, final Integer pageNumber, final Integer size) {
		// Validar ciudad
		validateParam(isStringNotBlank(cityToSearch), "El valor [ " + cityToSearch + " ] est?? vac??o o no es v??lido.");

		// Comprobar que el n??mero de p??gina y el tama??o de esta son v??lidos.
		checkPageNumber(pageNumber);
		checkPageSize(size);

		String listAccomodationsByCityQuery = "SELECT ac "
				+ "FROM AccomodationModel ac INNER JOIN ac.idAccomodationLocation al " + "WHERE LOWER(al.city) "
				+ "LIKE LOWER(:city)";

		TypedQuery<AccomodationModel> accomodations = em.createQuery(listAccomodationsByCityQuery,
				AccomodationModel.class);

		accomodations.setParameter("city", cityToSearch);

		// N??mero de alojamientos a mostrar
		accomodations.setMaxResults(size);

		return new PageImpl<AccomodationModel>(accomodations.getResultList());
	}

	/**
	 * Listado de alojamientos cercanos a unas coordenadas [ <code>lat</code> ,
	 * <code>lng</code> ]
	 * 
	 * Se especifica el radio de b??squeda en el par??metro <code>distance</code>.
	 * 
	 * Para realizar el c??lculo se utiliza la f??rmula de Haversine.
	 * 
	 * {@link #ACCOMODATION_LIMIT_RESULTS}
	 * 
	 * @param lat
	 * @param lng
	 * @param distance
	 * 
	 * @return
	 */
	@Override
	public List<AccomodationModel> findByNearby(final BigDecimal lat, final BigDecimal lng, final double distance) {
		// Validar latitud
		validateParam(isValidGeographicCoordinate(lat, true), "La latitud introducida no es v??lida.");

		// Validar latitud
		validateParam(isValidGeographicCoordinate(lng, false), "La longitud introducida no es v??lida.");

		// Validar radio b??squeda
		validateParam(isDoubleValidAndPositive(distance), "La distancia introducida no es v??lida.");

		String findByNearbyLocationQuery = "SELECT am "
				+ "FROM AccomodationModel am INNER JOIN am.idAccomodationLocation acloc " + "WHERE " + HAVERSINE_FORMULA
				+ " < :distance" + " ORDER BY " + HAVERSINE_FORMULA + " DESC";

		TypedQuery<AccomodationModel> accomodations = em.createQuery(findByNearbyLocationQuery,
				AccomodationModel.class);

		accomodations.setParameter("latitude", lat);
		accomodations.setParameter("longitude", lng);
		accomodations.setParameter("distance", distance);

		return accomodations.setMaxResults(ACCOMODATION_LIMIT_RESULTS).getResultList();
	}

	/**
	 * Actualizaci??n de los datos del alojamiento con n??mero de registro
	 * <code>regNumber</code>.
	 * 
	 * @param regNumber
	 * @param accomodationToUpdate
	 * 
	 * @return
	 */
	@Override
	@Modifying
	public AccomodationModel updateAccomodationById(final String regNumber,
			final AccomodationModel accomodationToUpdate) {
		// Validar n??mero registro alojamiento.
		validateParam(isStringNotBlank(regNumber), "El n??mero de registro est?? vac??o");

		// Detalles del alojamiento original
		AccomodationModel originalAccomodation = getAccomodationById(regNumber);

		// Validar modelo Alojamiento pasado como par??metro.
		validateParam(isNotNull(accomodationToUpdate), "Alguno de los datos del alojamiento a actualizar no es v??lido");

		// Comprobar si existe el alojamiento a actualizar
		validateParamNotFound(accomodationRepo.existsById(regNumber),
				"No existe un alojamiento con n??mero de registro " + regNumber);

		// Descripci??n
		originalAccomodation.setDescription(accomodationToUpdate.getDescription());

		// N??mero de habitaciones
		originalAccomodation.setNumOfBedRooms(accomodationToUpdate.getNumOfBedRooms());

		// N??mero de ba??os
		originalAccomodation.setNumOfBathRooms(accomodationToUpdate.getNumOfBathRooms());

		// N??mero de camas
		originalAccomodation.setNumOfBeds(accomodationToUpdate.getNumOfBeds());

		// N??mero de invitados
		originalAccomodation.setNumOfGuests(accomodationToUpdate.getNumOfGuests());

		// Precio por noche
		originalAccomodation.setPricePerNight(accomodationToUpdate.getPricePerNight());

		// Superficie de la vivienda
		originalAccomodation.setArea(accomodationToUpdate.getArea());

		// Propietario de la vivienda
		if (accomodationToUpdate.getIdUserHost() != null) {
			originalAccomodation.setIdUserHost(accomodationToUpdate.getIdUserHost());
		}

		return accomodationRepo.save(originalAccomodation);
	}

	/**
	 * Listado de alojamientos filtrando por la categor??a
	 * <code>accomodationCategory</code>.
	 * 
	 * @param accomodationCategory
	 * 
	 * @return
	 */
	@Override
	public List<AccomodationModel> findByCategory(final String accomodationCategory) {
		// Validar categoria
		validateParam(isStringNotBlank(accomodationCategory), "La categor??a introducida est?? vac??a o no es v??lida.");

		String findByAccomodationCategoryQuery = "SELECT am "
				+ "FROM AccomodationModel am INNER JOIN am.idAccomodationCategory acc "
				+ "WHERE acc.accomodationCategory = :category";

		TypedQuery<AccomodationModel> accomodations = em.createQuery(findByAccomodationCategoryQuery,
				AccomodationModel.class);

		accomodations.setParameter("category", accomodationCategory);

		return accomodations.getResultList();
	}

	/**
	 * Listado de alojamientos filtrando por un rango de precios comprendido entre
	 * <code>minPrice</code> y <code>maxPrice</code>.
	 * 
	 * @param minPrice
	 * @param maxPrice
	 * 
	 * @return
	 */
	@Override
	public List<AccomodationModel> findByPriceRange(final BigDecimal minPrice, final BigDecimal maxPrice) {
		// Validar precio m??nimo.
		validateParam(isBigDecimalValid(minPrice), "El precio m??nimo introducido no es v??lido.");

		// Validar precio m??ximo.
		validateParam(isBigDecimalValid(maxPrice), "El precio m??ximo introducido no es v??lido.");

		String findByAccomodationCategoryQuery = "SELECT am " + "FROM AccomodationModel am "
				+ "WHERE am.pricePerNight BETWEEN :minPrice and :maxPrice " + "ORDER BY am.pricePerNight DESC";

		TypedQuery<AccomodationModel> accomodations = em.createQuery(findByAccomodationCategoryQuery,
				AccomodationModel.class);

		accomodations.setParameter("minPrice", minPrice);
		accomodations.setParameter("maxPrice", maxPrice);

		return accomodations.getResultList();
	}

	/**
	 * Listado de alojamientos delimitado a <code>maxNumberOfAccomodations</code>
	 * resultados.
	 * 
	 * @see #validateParam
	 * 
	 * @param maxNumberOfAccomodations
	 * 
	 * @return
	 */
	@Override
	public List<AccomodationModel> findNAccomodations(final Integer maxNumberOfAccomodations) {
		// Validar n??mero m??ximo de alojamientos a mostrar.
		validateParam(isIntegerValidAndPositive(maxNumberOfAccomodations),
				"El n??mero m??ximo de resultados a mostrar no es v??lido.");

		TypedQuery<AccomodationModel> accomodations = em.createQuery("SELECT am FROM AccomodationModel am",
				AccomodationModel.class);

		accomodations.setMaxResults(maxNumberOfAccomodations);

		return accomodations.getResultList();
	}

	/**
	 * Listado de todos los alojamientos del usuario <code>userId</code>.
	 * 
	 * @param userId
	 * 
	 * @return
	 */
	@Override
	public List<AccomodationModel> findByUserId(final Integer userId) {
		// Validar id de usuario
		validateParam(isIntegerValidAndPositive(userId), "El id de usuario [ " + userId + " ] no es v??lido.");

		// Comprobar si el usuario existe
		validateParam(userRepo.existsById(userId), "No existe ning??n usuario host con el id " + userId);

		TypedQuery<AccomodationModel> accomodationsByUserId = em.createQuery(
				"SELECT am FROM AccomodationModel am WHERE am.idUserHost.id = :userId", AccomodationModel.class);

		accomodationsByUserId.setParameter("userId", userId);

		return accomodationsByUserId.getResultList();
	}

	@Transactional
	@Override
	public void removeAccomodationImage(final String regNumber, final Integer imageId) {
		// Validar n??mero de registro
		validateParam(isStringNotBlank(regNumber), "El n??mero de registro introducido est?? vac??o");

		// Comprobar que existe el alojamiento
		validateParamNotFound(accomodationRepo.existsById(regNumber),
				"El alojamiento con n??mero de registro [ " + regNumber + " ] no existe.");

		// Validar id de la imagen
		validateParam(isIntegerValidAndPositive(imageId), "El id de la imagen a borrar no es v??lido.");

		em.createQuery(
				"DELETE FROM AccomodationAccImageModel accim WHERE accim.accomodationAccImageId.idAccomodationImage.id = :imgId")
				.setParameter("imgId", imageId).executeUpdate();

		em.createQuery("DELETE FROM AccomodationImageModel aim WHERE aim.id = :imgId").setParameter("imgId", imageId)
				.executeUpdate();
	}

	@Transactional
	@Override
	public AccomodationModel addNewImageToExistingAccomodation(final String regNumber,
			final AccomodationImageModel imageToAdd) {

		// Validar n??mero de registro
		validateParam(isStringNotBlank(regNumber), "El n??mero de registro introducido est?? vac??o");

		// Comprobar que existe el alojamiento
		validateParamNotFound(accomodationRepo.existsById(regNumber),
				"El alojamiento con n??mero de registro [ " + regNumber + " ] no existe.");

		// Validar la imagen
		validateParam(isNotNull(imageToAdd), "La imagen a a??adir est?? vac??a.");

		// Crear la imagen
		em.createNativeQuery("INSERT INTO ACCOMODATION_IMAGE(IMG_URL) VALUES(:imgUrl)")
				.setParameter("imgUrl", imageToAdd.getImageUrl()).executeUpdate();

		// Obtener el id de la ??ltima imagen
		TypedQuery<AccomodationImageModel> lastImage = em.createQuery(
				"SELECT aim FROM AccomodationImageModel aim ORDER BY aim.id DESC", AccomodationImageModel.class);

		Integer lastImageId = lastImage.setMaxResults(1).getSingleResult().getId();

		// A??adir la imagen al alojamiento indicado
		em.createNativeQuery("INSERT INTO ACCOMODATION_ACC_IMAGE(ID_ACC, ID_ACC_IMAGE) VALUES(:regNumber, :imgId)")
				.setParameter("regNumber", regNumber).setParameter("imgId", lastImageId).executeUpdate();

		return accomodationRepo.findById(regNumber).get();
	}

	/**
	 * Listado de todas las ciudades donde se han publicado alojamientos en la app.
	 */
	@Override
	public List<String> findAllAccomodationCities() {
		String getAccomodationCitiesQuery = "SELECT DISTINCT al.city "
				+ "FROM AccomodationModel am INNER JOIN am.idAccomodationLocation al";

		TypedQuery<String> citiesToReturn = em.createQuery(getAccomodationCitiesQuery, String.class);

		return citiesToReturn.getResultList();
	}

	/**
	 * Listado filtrado de los alojamientos de la aplicaci??n.
	 * 
	 * Los filtros pueden ser:
	 * <ul>
	 * <li>Rango precios (M??nimo, m??ximo).</li>
	 * <li>N??mero de camas</li>
	 * <li>N??mero de habitaciones</li>
	 * <li>N??mero de ba??os</li>
	 * <li>N??mero de hu??spedes</li>
	 */
	@Override
	public List<AccomodationModel> findAllByMultipleFilters(final Optional<BigDecimal> minPrice,
			final Optional<BigDecimal> maxPrice, final Optional<Integer> beds, final Optional<Integer> bedrooms,
			final Optional<Integer> bathrooms, final Optional<Integer> guests) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<AccomodationModel> query = builder.createQuery(AccomodationModel.class);

		Root<AccomodationModel> root = query.from(AccomodationModel.class);

		List<Predicate> predicates = new ArrayList<>();

		// Rango de precios [min, max]
		if (minPrice.isPresent() && maxPrice.isPresent() && minPrice.get().compareTo(BigDecimal.ZERO) > 0
				&& maxPrice.get().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(builder.between(root.get("pricePerNight"), minPrice.get(), maxPrice.get()));

		} else if (!minPrice.isPresent() && maxPrice.isPresent() && maxPrice.get().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(builder.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice.get()));

		} else if (minPrice.isPresent() && !maxPrice.isPresent() && minPrice.get().compareTo(BigDecimal.ZERO) > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice.get()));
		}

		// N??mero de camas
		if (beds.isPresent() && beds.get() > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("numOfBeds"), beds.get()));
		}

		// N??mero de habitaciones
		if (bedrooms.isPresent() && bedrooms.get() > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("numOfBedRooms"), bedrooms.get()));
		}

		// N??mero de ba??os
		if (bathrooms.isPresent() && bathrooms.get() > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("numOfBathRooms"), bathrooms.get()));
		}

		// Hu??spedes
		if (guests.isPresent() && guests.get() > 0) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("numOfGuests"), guests.get()));
		}

		query.where(builder.and(predicates.toArray(new Predicate[0])));

		return em.createQuery(query.select(root)).getResultList();
	}

	/**
	 * Listado de todos los alojamientos cuya ciudad contiene el criterio de
	 * b??squeda.
	 * 
	 * @param match
	 * 
	 * @return
	 */
	@Override
	public List<String> findByCityMatch(final String match) {

		// Validar criterio b??squeda
		validateParam(isStringNotBlank(match), "Introduce una ciudad a buscar");

//		String findCityNatchQuery = "SELECT am FROM AccomodationModel am INNER JOIN am.idAccomodationLocation alm WHERE aml.city LIKE %:match% ";
//
//		TypedQuery<AccomodationModel> accomodations = em.createQuery(findCityNatchQuery, AccomodationModel.class);
//
//		accomodations.setParameter("match", match);

		return accomodationRepo.findBySearchCriteria(match);
	}

}
