const palettes=[['#132b4d','#30d9ff','#7c6cff'],['#3a1737','#ff6f91','#ffbd6f'],['#153b35','#b8f35b','#30d9ff'],['#24194a','#a88cff','#ff76ae'],['#153044','#67e8f9','#f9a8d4'],['#4b2138','#ff9f68','#ffe29a']]
const names=[['星际漫游','Nebula Kid','电子'],['凌晨四点','林间信号','独立流行'],['蓝色脉冲','Echo Unit','氛围'],['失重浪漫','Naomi','R&B'],['月球来信','北纬三十','民谣'],['霓虹雨季','Pixel Heart','Synthwave'],['云层之上','Aerial Club','后摇'],['慢速燃烧','June Tape','爵士'],['透明城市','Mono Park','另类'],['海岸公路','Sunday Drive','流行']]
export const musicList=names.map((x,i)=>({id:i+1,musicName:x[0],singerName:x[1],typeName:x[2],musicDesc:['一段关于夜色、远方和未读消息的声音切片。','合成器与真实鼓点交叠，像城市灯光缓慢掠过车窗。','把寻常日子折叠进旋律里，留下一点柔软回声。'][i%3],ar:[1.02,1.26,.82,1.15,.9,1.34,1.08,.78,1.2,.94][i],palette:palettes[i%palettes.length]}))
export const categories=[{id:1,typeName:'电子脉冲',typeDesc:'合成器、节拍与未来主义声场',palette:palettes[0],children:[{typeName:'Synthwave'},{typeName:'氛围电子'}]},{id:2,typeName:'城市漫游',typeDesc:'适合夜行与长距离通勤的声音',palette:palettes[1],children:[{typeName:'独立流行'},{typeName:'R&B'}]},{id:3,typeName:'自然回响',typeDesc:'原声器乐与开阔的空间感',palette:palettes[2],children:[{typeName:'民谣'},{typeName:'后摇'}]}]
export const tags=['夜行','合成器','独立','治愈','律动','公路','雨天','器乐'].map((tagName,id)=>({id:id+1,tagName,tagDesc:'用于发现相近声音与情绪'}))
export const detail=id=>{const m=musicList.find(v=>v.id===Number(id))||musicList[0];return {...m,albumTitle:'未抵达的光',releaseDate:'2026-08-16',coverImages:[],tags:['夜行','合成器','城市'],createTime:'2026-08-16 20:12:05',updateTime:'2026-08-24 09:42:10'}}
export const statistics={year:'2026',yearCount:1842,month:'08',monthCount:286,day:'26',dayCount:18}

export function mockResolve(config,scope){
  const url=config.url||''; const ok=result=>Promise.resolve({status:{code:1001,msg:'OK'},result})
  if(url.includes('/music/list')) return ok(scope==='app'?{list:musicList,isEnd:true,wp:''}:{list:musicList,total:musicList.length,pageSize:10})
  if(url.includes('/music/info')) return ok(detail(config.params?.id))
  if(url.includes('/music/statistics')) return ok(statistics)
  if(url.includes('/category/list')) return ok({list:categories})
  if(url.includes('/tag/list')) return ok({list:tags.map(v=>({id:v.id,tag:v.tagName,tagName:v.tagName,tagDesc:v.tagDesc}))})
  if(url.includes('/user/login/app')) return ok({userInfo:{userId:1,phone:config.params?.phone,name:'声场旅人',gender:1,avatar:''},sign:'mock-sign-v1'})
  if(url.includes('/user/register/app')) return ok({userInfo:{userId:2,phone:config.params?.phone,name:config.params?.name,gender:config.params?.gender,avatar:''},sign:'mock-sign-v1'})
  if(url.includes('/user/login/web')) return ok({userId:1,userGender:1,userName:'MusicCard Admin',userPhone:config.params?.phone,userAvatar:''})
  if(url.includes('/user/logout/web')) return ok('退出成功')
  if(url.includes('/sms/send-batch')) return ok(String(config.params?.phones||'').split(',').map(phone=>({phone,ok:true,code:'OK',requestId:'MOCK-'+Date.now()})))
  if(url.includes('/sms/send-async')) return ok({accepted:true,taskId:Date.now(),status:'PENDING'})
  if(url.includes('/sms/send-sync')) return ok({ok:true,verifyCode:'385201',code:'OK',requestId:'MOCK-'+Date.now()})
  return ok('操作成功')
}
