import { appRequest, userRequest } from './http'
export const appApi={
  musicList:params=>appRequest({url:'/music/list',method:'get',params}),
  musicInfo:id=>appRequest({url:'/music/info',method:'get',params:{id}}),
  categories:()=>appRequest({url:'/category/list',method:'get'}),
  login:params=>userRequest({url:'/user/login/app',method:'get',params}),
  register:params=>userRequest({url:'/user/register/app',method:'get',params})
}
