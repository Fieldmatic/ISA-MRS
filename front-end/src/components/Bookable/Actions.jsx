import React from 'react'
import { useParams } from 'react-router-dom'
import ActionPanel from './ActionPanel';
import Header from '../../Header';

function Actions(){
    let {id} = useParams();

    return (
        <div>
            <Header/>
            <ActionPanel bookableId = {id}/>
            //
        </div>
    )


}

export default Actions