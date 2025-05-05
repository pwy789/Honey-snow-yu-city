//登录时的类型声明
export type EmployeeLoginForm={
   shopAccount:string,
   shopPassword:string,
   username:string,
   userPassword:string
}
//员工状态管理类型
export type EmployeeInfo={
   id:number,
   name:string,
   phone:string,
   shopId:string,
   identify:number,
   token:string
}
//员工信息类型
export type EmployeeType={
   id:string,
   name:string,
   phone:string,
   username:string,
   
}