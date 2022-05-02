import { Route, Routes } from "react-router-dom";
import NotFound from "./NotFound";
import Home from "./Home";
import AddAdventureForm from "./Components/Adventure/AddAdventureForm";
import UpdateAdventureForm from "./Components/Adventure/UpdateAdventureFrom"
import ClientProfile from "./components/Client/ClientProfile";
import Cottage from "./components/cottage/Cottage";
import EditCottage from "./components/cottage/EditCottage";
import Boat from "./components/Boat/Boat";


const Views = () => {
  return (
    <Routes>
      <Route index element = {<Home/>}/>
      <Route path = "/addAdventure" element = {<AddAdventureForm />}/>
      <Route path = "/editAdventure/:id" element = {<UpdateAdventureForm />}/>
      <Route path = "/addCottage" element = {<Cottage/>}/>
      <Route path = "/editCottage/:id" element = {<EditCottage/>}/>
      <Route path = "/addExperience" element = {<div>Pecanje</div>}/>
      <Route path = "/addExperience" element = {<div>Pecanje</div>}/>
      <Route path = "/editProfile" element = {< ClientProfile />}/>
      <Route path = "/addBoat" element = {<Boat/>}/>
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

export default Views;