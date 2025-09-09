// File: src/services/paymentService.js
import api from "./api";

/**
 * Initiates a new payment session.
 * @param {Object} paymentData - The rental payment details.
 * @returns {Promise<Object>} The created PaymentResponseDto.
 */
export const makePayment = async (paymentData) => {
  const response = await api.post("/payments", paymentData);
  return response.data;
};

/**
 * Fetches all payments for a given user, including discount fields.
 * @param {number|string} userId - The ID of the user.
 * @returns {Promise<Array>} Array of PaymentResponseDto objects.
 */
export const getUserPayments = async (userId) => {
  const response = await api.get("/payments", {
    params: { userId },
  });
  return response.data;
};

/**
 * Fetches payment status for a user (alias of getUserPayments).
 * Kept for backward compatibility.
 * @param {number|string} userId - The ID of the user.
 * @returns {Promise<Array>} Array of PaymentResponseDto objects.
 */
export const getPaymentStatus = getUserPayments;

/**
 * Fetches a fee + discount estimate for a hypothetical rental.
 * @param {Object} params – { userId, carId, rentalDate, returnDate }
 * @returns {Promise<DiscountEstimateDto>}
 */
export const getDiscountEstimate = async ({ userId, carId, rentalDate, returnDate }) => {
  const response = await api.get("/payments/estimate", {
    params: { userId, carId, rentalDate, returnDate },
  });
  return response.data;
  
};
export const getAllPayments = async () => {
  const response = await api.get("/payments/all"); // ✅ Now valid endpoint
  return response.data;
};

