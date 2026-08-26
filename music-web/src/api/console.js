import { consoleRequest,consoleUserRequest,asForm } from './http'
const form=(url,method,params)=>consoleRequest({url,method,data:asForm(params),headers:{'Content-Type':'application/x-www-form-urlencoded'}})
export const consoleApi={
  login:params=>consoleUserRequest({url:'/user/login/web',method:'get',params}),logout:()=>consoleUserRequest({url:'/user/logout/web',method:'post'}),statistics:()=>consoleRequest({url:'/music/statistics',method:'get'}),
  musicList:params=>consoleRequest({url:'/music/list',method:'get',params}),musicInfo:id=>consoleRequest({url:'/music/info',method:'get',params:{id}}),
  saveMusic:data=>form(data.id?'/music/update':'/music/create',data.id?'put':'post',data),deleteMusic:id=>consoleRequest({url:'/music/delete',method:'delete',params:{id}}),
  categories:()=>consoleRequest({url:'/category/list',method:'get'}),saveCategory:data=>form(data.id?'/category/update':'/category/create',data.id?'put':'post',data),deleteCategory:id=>consoleRequest({url:'/category/delete',method:'delete',params:{id}}),
  tags:()=>consoleRequest({url:'/tag/list',method:'get'}),saveTag:data=>form(data.id?'/tag/update':'/tag/create',data.id?'put':'post',data),deleteTag:id=>consoleRequest({url:'/tag/delete',method:'delete',params:{id}}),
  // Backend Console SmsController exposes GET /sms/send-{mode} with query params.
  sendSms:(mode,params)=>consoleRequest({url:'/sms/send-'+mode,method:'get',params})
}
