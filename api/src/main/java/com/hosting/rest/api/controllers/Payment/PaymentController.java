package com.hosting.rest.api.controllers.Payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hosting.rest.api.exceptions.IllegalArguments.IllegalArgumentsCustomException;
import com.hosting.rest.api.models.Payment.PaymentModel;
import com.hosting.rest.api.services.Payment.PaymentServiceImpl;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	@Autowired
	private PaymentServiceImpl paymentService;

	@PostMapping("new")
	public PaymentModel addNewPaymentMethod(@RequestBody final PaymentModel paymentModel) {
		if (paymentModel == null) {
			throw new IllegalArgumentsCustomException("Los datos para el método de pago a crear no son válidos.");
		}

		return paymentService.addNewPayment(paymentModel);
	}

	@DeleteMapping("{paymentId}")
	public void removePaymentMethod(@PathVariable(value = "paymentId") final String paymentId) {

		try {
			paymentService.removePaymentById(Integer.parseInt(paymentId));

		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentsCustomException(
					"El id del método de pago [ " + paymentId + " ] no es un número.");
		}

	}

	@GetMapping("all")
	public List<PaymentModel> listAllPaymentMethods() {
		return paymentService.findAllPayments();
	}

	@GetMapping("{bookingId}")
	public PaymentModel getPaymentMethodFromBooking(@PathVariable(value = "bookingId") final String bookingId) {
		PaymentModel paymentToReturn = null;

		try {
			paymentToReturn = paymentService.findByBookingId(Integer.parseInt(bookingId));

		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentsCustomException("El id de la reserva [ " + bookingId + " ] no es un número.");
		}

		return paymentToReturn;
	}
}
