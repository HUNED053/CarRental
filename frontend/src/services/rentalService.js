import api from "./api";

export const rentCar = async (rentalData) => {
  const response = await api.post("/rentals", rentalData);
  return response.data;
};

export const getUserRentals = async (userId, isActive) => {
    const response = await api.get(`/rentals?isActive=${isActive}&userId=${userId}`);
    return response.data;
  };

  
export const returnRental = async (rentalId) => {
    const response = await api.post(`/rentals/${rentalId}/return`);
    return response.data;
  };