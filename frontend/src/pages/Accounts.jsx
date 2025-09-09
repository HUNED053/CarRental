import { useEffect, useState } from "react";//hello
import axios from "../services/api";
import Navbar from "../components/Navbar";
import { toast } from "react-toastify";
import { FaTrash, FaSave, FaEdit, FaLock } from "react-icons/fa";
import "../App.css";

const Accounts = () => {
  const [users, setUsers] = useState([]);
  const [editUserId, setEditUserId] = useState(null);
  const [editedFields, setEditedFields] = useState({});

  const fetchUsers = async () => {
    const res = await axios.get("/users");
    setUsers(res.data);
  };

  const toggleRole = async (id) => {
    const res = await axios.put(`/users/${id}/toggle-role`);
    setUsers(users.map(user => user.id === id ? res.data : user));
  };

  const deleteUser = async (id) => {
    console.log("Delete function called for id:", id); // Debug
  
    const confirmDelete = window.confirm("Are you sure you want to delete this user?");
    console.log("Confirm result:", confirmDelete); // Debug
  
    if (!confirmDelete) return;
  
    try {
      const res = await axios.delete(`/users/${id}`);
      console.log("Backend delete response:", res); // Debug
  
      setUsers(users.filter(user => user.id !== id));
      toast.success("Deleted successfully!", { autoClose: 3000 });
    } catch (err) {
      console.error("Error deleting user:", err);
      toast.error("Failed to delete user. Please try again.");
    }
  };
  

  const handleEditClick = (user) => {
    setEditUserId(user.id);
    setEditedFields({
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
    });
  };

  const handleInputChange = (field, value) => {
    setEditedFields(prev => ({ ...prev, [field]: value }));
  };

  const handleSaveClick = async (id) => {
    try {
      const res = await axios.put(`/users/${id}`, editedFields);
      setUsers(users.map(user => user.id === id ? res.data : user));
      setEditUserId(null);
      toast.success("User updated successfully");
    } catch (err) {
      console.error("Error updating user:", err);
      toast.error("Failed to update user");
    }
  };

  const handleResetPassword = async (userId) => {
    const newPassword = prompt("Enter a new password:");
    if (!newPassword) return;

    try {
      await axios.put(`/users/${userId}/reset-password`, { newPassword });
      toast.success("Password reset successfully");
    } catch (err) {
      console.error("Password reset failed:", err);
      toast.error("Failed to reset password");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div>
      <Navbar />
      <div className="accounts-container">
        <h2>User Accounts</h2>
        <table className="accounts-table">
          <thead>
            <tr>
              <th>Email</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Role (click to toggle)</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>
                  {editUserId === user.id ? (
                    <input
                      type="email"
                      value={editedFields.email}
                      onChange={(e) => handleInputChange("email", e.target.value)}
                    />
                  ) : (
                    user.email
                  )}
                </td>
                <td>
                  {editUserId === user.id ? (
                    <input
                      type="text"
                      value={editedFields.firstName}
                      onChange={(e) => handleInputChange("firstName", e.target.value)}
                    />
                  ) : (
                    user.firstName
                  )}
                </td>
                <td>
                  {editUserId === user.id ? (
                    <input
                      type="text"
                      value={editedFields.lastName}
                      onChange={(e) => handleInputChange("lastName", e.target.value)}
                    />
                  ) : (
                    user.lastName
                  )}
                </td>
                <td className="role-cell" onClick={() => toggleRole(user.id)}>
                  {user.role}
                </td>
                <td>
                  {editUserId === user.id ? (
                    <button onClick={() => handleSaveClick(user.id)} className="save-btn" title="Save">
                      <FaSave />
                    </button>
                  ) : (
                    <button onClick={() => handleEditClick(user)} className="edit-btn" title="Edit">
                      <FaEdit />
                    </button>
                  )}
                  <button onClick={() => handleResetPassword(user.id)} className="reset-btn" title="Reset Password">
                    <FaLock />
                  </button>
                  <button onClick={() => deleteUser(user.id)} className="delete-btn" title="Delete User">
                    <FaTrash />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Accounts;
