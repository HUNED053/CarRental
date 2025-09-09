import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";
import { getCarById, deleteCar } from "../services/carService";
import Navbar from "../components/Navbar"; 

const CarDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const [car, setCar] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(null);


  useEffect(() => {
    const fetchCar = async () => {
      try {
        const data = await getCarById(id);
        setCar(data);
      } catch (error) {
        console.error("Error fetching car details:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchCar();
  }, [id]);

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this car?")) {
      try {
        await deleteCar(id);
        navigate("/cars");
      } catch (error) {
        console.error("Error deleting car:", error);
      }
    }
  };

  if (loading) return <p className="loading">Loading...</p>;
  if (!car) return <p className="error">Car not found.</p>;

  return (
    <div>
      <Navbar />
      <div className="car-details-container">
      <img
                src={`http://localhost:8080${car.carImage}`}
                alt={`${car.brand} ${car.model}`}
                className="car-image"
                onClick={() => setSelectedImage(`http://localhost:8080${car.carImage}`)}
              />
        <h2>{car.brand} {car.model}</h2>
        <p><strong>Type:</strong> {car.type}</p>
        <p><strong>Daily Fee:</strong> ${car.dailyFee}/day</p>
        <p><strong>Available Inventory:</strong> {car.inventory} cars</p>
        

        <div className="car-actions">
          <button className="btn rent-btn">Rent Car</button>

          {user?.role === "MANAGER" && (
            <>
              <button onClick={() => navigate(`/edit-car/${id}`)} className="btn edit-btn">
                Edit
              </button>
              <button onClick={handleDelete} className="btn delete-btn">
                Delete
              </button>
            </>
          )}
        </div>
      </div>
      {selectedImage && (
        <div className="modal" onClick={() => setSelectedImage(null)}>
          <span className="modal-close" onClick={() => setSelectedImage(null)}>&times;</span>
          <img src={selectedImage} alt="Car full view" />
        </div>
      )}
    </div>
  );
};

export default CarDetails;
