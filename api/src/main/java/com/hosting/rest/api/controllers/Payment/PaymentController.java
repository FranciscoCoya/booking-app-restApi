package com.hosting.rest.api.controllers.Payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hosting.rest.api.models.Payment.PaymentModel;
import com.hosting.rest.api.services.Payment.PaymentServiceImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

	@Autowired
	private PaymentServiceImpl paymentService;

	@PostMapping(name = "/new")
	public PaymentModel addNewPaymentMethod(@RequestBody PaymentModel paymentModel) {
		return paymentService.addNewPayment(paymentModel);
	}

	@PutMapping(name = "{paymentId}")
	public PaymentModel updatePaymentModel(@PathVariable(name = "paymentId") PaymentModel paymentModel) {
		return paymentService.updatePaymentById(paymentModel);
	}

	// TODO: Eliminar un método de pago.
	@DeleteMapping(name = "{paymentId}")
	public void removePaymentMethod(@PathVariable(name = "paymentId") Integer paymentId) {
		paymentService.removePaymentById(paymentId);
	}

	// TODO: Listar todos los métodos de pago disponibles.
	@GetMapping(name = "all")
	public List<PaymentModel> listAllPaymentMethods() {
		return paymentService.listAllPayments();
	}

	// TODO: Obtener el método de pago de una reserva realizada.
	/*@GetMapping(name = "{bookingId}")
	public PaymentModel getPaymentMethodFromBooking(@PathVariable(name = "bookingId") Integer bookingId) {
		return paymentService.getPaymentFromBooking(bookingId);
	}*/
}