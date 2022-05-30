import React from 'react'
import { Dropzone, FileItem, FullScreenPreview } from "@dropzone-ui/react";
import LoginRegisterService from "../../services/LoginRegisterService"
import Header from "../../Header";
import { useNavigate } from "react-router-dom";

export default function OwnerRegistration() {
    const [formData, setFormData] = React.useState (
        {
          name : "",
          surname: "",
          address: {
            street: "",
            city: "",
            state: "",
            zipCode: ""
          },
          username : "",
          password : "",
          passwordRetype: "",
          email : "",
          phoneNumber: "",
          registrationExplanation : "",
          role : "ROLE_COTTAGE_OWNER"
        }
  
    )

    let navigate = useNavigate();

    const [files, setFiles] = React.useState([]);

    const [imageSrc, setImageSrc] = React.useState(undefined);

    const updateFiles = (incommingFiles) => {
        console.log("incomming files", incommingFiles);
        setFiles(incommingFiles);
    };

    const onDelete = (id) => {
        setFiles(files.filter((x) => x.id !== id));
    };

    const handleSee = (imageSource) => {
        setImageSrc(imageSource);
    };

    const handleClean = (files) => {
        console.log("list cleaned", files);
    };

    function handleChange(event) {
        const {name, value} = event.target
        setFormData(prevFormData => {
        return {
            ...prevFormData,
            [name]: value
        }
        })
    }

    function handleAddressChange(event) {
        const {name, value} = event.target
        const address = formData.address
        setFormData(prevFormData => {
            return {
                ...prevFormData,
                address: {
                        ...address,
                        [name]:value
                        }
            }
        })
    }
    
    function handleSubmit(event){
        event.preventDefault()
        if (formData.password !== formData.passwordRetype) {
            alert("Passwords aren't matching")
            return
        }
        let data = new FormData()
        const ownerJson = getOwnerJson();
        data.append("owner", ownerJson)
        files.map((file) => {
            data.append("files", file.file)
        })
        LoginRegisterService.registerOwner(data).then(response => {
            if (response.status === 200) alert("Success");
            navigate("/");
        });
    }
    
    return (
        <div>
            <Header />
            <div className="form-container">
                <form className="form" onSubmit={handleSubmit}>
                <h1 className='form--header'>Owner Registration</h1>
                <input
                    className="form--input"
                    type = "text"
                    placeholder = "Name"
                    onChange = {handleChange}
                    name = "name"
                    value = {formData.name}   
                />
                <input
                    className="form--input"
                    type = "text"
                    placeholder = "Surname"
                    onChange = {handleChange}
                    name = "surname"
                    value = {formData.surname}   
                />
                <input 
                    className="form--input"
                    type = "text"
                    placeholder = "State"
                    onChange = {handleAddressChange}
                    name = "state"
                    value = {formData.address.state}          
                />
                <input 
                    className="form--input"
                    type = "text"
                    placeholder = "City"
                    onChange = {handleAddressChange}
                    name = "city"
                    value = {formData.address.city}          
                />
                <input 
                    className="form--input"
                    type = "text"
                    placeholder = "Zip"
                    onChange = {handleAddressChange}
                    name = "zipCode"
                    value = {formData.address.zipCode}          
                />
                <input 
                    className="form--input"
                    type = "text"
                    placeholder = "Street"
                    onChange = {handleAddressChange}
                    name = "street"
                    value = {formData.address.street}          
                />
                <input
                    className="form--input"
                    type = "text"
                    placeholder = "Username"
                    onChange = {handleChange}
                    name = "username"
                    value = {formData.username}   
                />
                <input
                    className="form--input"
                    type = "password"
                    placeholder = "Password"
                    onChange = {handleChange}
                    name = "password"
                    value = {formData.password}   
                />
                <input
                    className="form--input"
                    type = "password"
                    placeholder = "Retype Password"
                    onChange = {handleChange}
                    name = "passwordRetype"
                    value = {formData.passwordRetype}   
                />
                <input
                    className="form--input"
                    type = "text"
                    placeholder = "Email"
                    onChange = {handleChange}
                    name = "email"
                    value = {formData.email}   
                />
                <input
                    className="form--input"
                    type = "text"
                    placeholder = "Phone number"
                    onChange = {handleChange}
                    name = "phoneNumber"
                    value = {formData.phoneNumber}   
                />
                <label className="type--label">Owner Type:</label>
                <select 
                        className="form--type"
                        value={formData.role}
                        onChange={handleChange}
                        name="role"
                    >
                        <option value="ROLE_COTTAGE_OWNER">Cottage Owner</option>
                        <option value="ROLE_BOAT_OWNER">Boat Owner</option>
                    </select>
                <textarea 
                className="form--input-area"
                placeholder = "Registration explanation"
                onChange = {handleChange}
                value = {formData.registrationExplanation}
                name = "registrationExplanation"
                />
                <Dropzone
                    style={{ minWidth: "95%", margin:"20px", fontSize:"18px" }}
                    onChange={updateFiles}
                    minHeight="20vh"
                    onClean={handleClean}
                    value={files}
                    maxFiles={1}
                    header={true}
                    maxFileSize={5000000}
                >
                    {files.map((file) => (
                    <FileItem
                        {...file}
                        key={file.id}
                        onDelete={onDelete}
                        onSee={handleSee}
                        resultOnTooltip
                        preview
                        info
                        hd
                    />
                    ))}
                    <FullScreenPreview
                    imgSource={imageSrc}
                    openImage={imageSrc}
                    onClose={(e) => handleSee(undefined)}
                    />
                </Dropzone>
                <button className="form--submit">Submit</button>  
            </form>
        </div>
    </div>
    )

    function registerOwner(data) {
            RegistrationService.addCottageOwner(data).then((response) => {
                alert(response.data);
                setRedirect("/");
            }).catch((err) => {
                alert(err.response.data);
            });
    }

    function getOwnerJson() {
        const json = JSON.stringify(formData);
        const ownerJson = new Blob([json], {
            type: 'application/json'
        });
        return ownerJson;
    }
}