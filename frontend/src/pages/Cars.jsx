import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import AuthContext from "../context/AuthContext";
import { getAllCars, deleteCar } from "../services/carService";
import { toast } from "react-toastify";
import Navbar from "../components/Navbar";
import "../App.css";

const Cars = () => {
  const { user } = useContext(AuthContext);
  const [cars, setCars] = useState([]);
  const [selectedImage, setSelectedImage] = useState(null);


  useEffect(() => {
    const fetchCars = async () => {
      try {
        const data = await getAllCars();
        setCars(data);
      } catch (error) {
        console.error("Error fetching cars:", error);
      }
    };

    fetchCars();
  }, []);

  const handleDelete = async (carId) => {
    if (window.confirm("Are you sure you want to delete this car?")) {
      try {
        await deleteCar(carId);
        setCars(cars.filter((car) => car.id !== carId));
        toast.success("Car deleted successfully!");
      } catch (error) {
        toast.error("Error deleting car.");
      }
    }
  };

  return (
    <div>
      <Navbar />
      {user && user.firstName && (
          <div className="greeting">
            <p>Hi, {user.firstName}</p>
          </div>
        )}
      <div className="cars-container">
        <h1>Available Cars</h1>

        {user?.role === "MANAGER" && (
          <Link to="/add-car" className="btn add-car-btn">
            + Add New Car
          </Link>
        )}

        <div className="car-grid">
          {cars.map((car) => (
            
            <div key={car.id} className="car-card">
              <img
                src={`http://localhost:8080${car.carImage}`}
                alt={`${car.brand} ${car.model}`}
                className="car-image"
                onClick={() => setSelectedImage(`http://localhost:8080${car.carImage}`)}
              />
              <h3>{car.brand} {car.model}</h3>
              <p><strong>Type:</strong> {car.type}</p>
              <p><strong>Daily Fee:</strong> ${car.dailyFee}/day</p>
              <p><strong>Available:</strong> {car.inventory} cars</p>

              <div className="car-actions">
                {user?.role === "MANAGER" && (
                  <>
                    <Link to={`/edit-car/${car.id}`} className="btn edit-btn">
                      Edit
                    </Link>
                    <button onClick={() => handleDelete(car.id)} className="btn delete-btn">
                      Delete
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
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

export default Cars;
