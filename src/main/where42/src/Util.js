import Swal from "sweetalert2";
import './Util.css';

export async function Alert(str){
    const isMobile = window.innerWidth <= 930;
    if (isMobile) {
        alert(str);
    } else {
        await Swal.fire(
            {
                title: str,
                allowEnterKey: true,
                allowOutsideClick: true,
                focusConfirm: true
            });
    }
}

export function AlertEvent(str, num){
    const isMobile = window.innerWidth <= 930;
    str = str.replace(/n/g, num);

    if (isMobile) {
        alert(str);
    } else {
        Swal.fire(
            {
                title: str,
                allowEnterKey: true,
                allowOutsideClick: true,
                focusConfirm: true
            });
    }
}

export function AlertReload(str){
    const isMobile = window.innerWidth <= 930;
    if (isMobile)
    {
        alert(str);
        window.location.reload();
    }
    else
        Swal.fire(
            {title : str,
                allowEnterKey: true,
                allowOutsideClick: true,
                focusConfirm: true
            }).then((res)=>{
                if (res.isConfirmed)
                    window.location.reload();
        })
}

export async function Confirm(str, text)
{
    const isMobile = window.innerWidth <= 930;
    if (isMobile)
    {
        if (window.confirm(str))
            return (true);
        else
            return (false);
    }
    else {
        const swalWithButtons = Swal.mixin({
            customClass: {
                confirmButton: 'btn btn-success',
                cancelButton: 'btn btn-danger'
            },
            buttonsStyling: true,
            allowEnterKey: false
        })
        return (swalWithButtons.fire({
            title: str,
            showCancelButton: true,
            confirmButtonText: text,
            cancelButtonText: '취소'
        }));
    }
}

export async function ConfirmEval(str, text)
{
    const isMobile = window.innerWidth <= 930;
    if (isMobile)
    {
        str = str.replace(/<br\/>/g, '\n');
        if (window.confirm(str))
            return (true);
        else
            return (false);
    }
    else {
        const swalWithButtons = Swal.mixin({
            customClass: {
                confirmButton: 'btn btn-success',
                cancelButton: 'btn btn-danger'
            },
            buttonsStyling: true,
            allowEnterKey: false
        })
        return (swalWithButtons.fire({
            html: str,
            showCancelButton: true,
            confirmButtonText: text,
            cancelButtonText: '취소'
        }));
    }
}

export async function AlertInput(str, value) {
    const isMobile = window.innerWidth <= 930;
    if (isMobile) {
        let resName = prompt(str, value);
        return ({
            value: resName
        })
    }
    else {
        const resName = await Swal.fire({
            title: str,
            input: 'text',
            inputValue: value,
            inputAttributes: {
                maxlength: 10
            },
            showCancelButton: true,
            cancelButtonText: '취소',
            confirmButtonText: '확인',
            inputValidator: (gName) => {
                if (gName === "즐겨찾기" || gName === "기본" || gName === "친구 목록" || gName === "" ||
                gName.split(' ').length - 1 === gName.length) {
                    return "사용할 수 없는 그룹명입니다."
                }
            }
        });
        if (resName)
            return (resName)
    }
}