import React from 'react';
import './Loading.css';

const Loading=()=> {
    return (
        <div id={"Loading"}>
            {/*<div id={"LoadingContent"}>정보를 불러오는 중입니다.<br/> 조금만 더 기다려주세요<br/> 제발 제발...</div>*/}
            <div id={"LoadingContent"}>로딩중......</div>
            <div id="LoadingCharacter"/>
        </div>
    )
}

export default Loading;