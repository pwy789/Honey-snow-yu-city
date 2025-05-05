import http from "@/utils/http"

export const uploadImgAPI=(data:FormData,theme:string)=>{
  return http({
    url:'/upload',
    method:'POST',
    data,
    params:{
      theme
    },
   headers:{
    'Content-Type':'multipart/form-data'
  }
  })
}