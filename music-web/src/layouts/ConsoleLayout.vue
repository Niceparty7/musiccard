<script setup>
	import {
		ref
	} from 'vue'
	import {
		useRoute
	} from 'vue-router'
	import { ElMessage, ElMessageBox } from 'element-plus'
	import BrandMark from '../components/BrandMark.vue'
	import { consoleApi } from '../api/console'
	const collapsed = ref(false);
	const logoutLoading = ref(false)
	const route = useRoute()
	const items = [
		['/console/dashboard', '⌁', '总览'],
		['/console/music', '◉', '音乐内容'],
		['/console/categories', '◇', '分类声场'],
		['/console/tags', '⌗', '标签系统'],
		['/console/sms', '↯', '短信实验室']
	]
	async function logout() {
		if (logoutLoading.value) return
		try {
			await ElMessageBox.confirm('退出后当前控制台会话将立即失效，确定退出吗？', '退出登录', {
				confirmButtonText: '确认退出',
				cancelButtonText: '取消',
				type: 'warning'
			})
		} catch (error) {
			if (error === 'cancel' || error === 'close') return
			throw error
		}
		logoutLoading.value = true
		try {
			await consoleApi.logout()
			localStorage.removeItem('musiccard_console_user')
			ElMessage.success('已安全退出')
			window.location.replace('/console/login')
		} catch (error) {
			ElMessage.error(error?.message || '退出失败，请稍后重试')
		} finally {
			logoutLoading.value = false
		}
	}
</script>
<template>
	<div class="console-shell" :class="{collapsed}">
		<aside>
			<BrandMark />
			<div class="signal"><i></i><span>CORE ONLINE</span></div>
			<nav><router-link v-for="i in items" :key="i[0]"
					:to="i[0]"><b>{{i[1]}}</b><span>{{i[2]}}</span></router-link></nav><button class="collapse"
				@click="collapsed=!collapsed">{{collapsed?'→':'← 收起'}}</button>
		</aside>
		<section class="workspace">
			<header><button class="mobile-menu" @click="collapsed=!collapsed">☰</button>
				<div><span class="eyebrow">MUSICCARD / CONTROL
						ROOM</span><strong>{{items.find(i=>route.path.startsWith(i[0]))?.[2]||'控制台'}}</strong></div>
				<div class="operator"><span class="operator-info"><b>MusicCard Admin</b><small>超级管理员</small></span><i>MC</i><button class="logout" type="button" :disabled="logoutLoading" aria-label="退出登录" @click="logout"><span>{{logoutLoading?'退出中':'退出登录'}}</span><b>↗</b></button></div>
			</header>
			<main><router-view /></main>
		</section>
	</div>
</template>
<style scoped>
	.console-shell {
		min-height: 100vh;
		background: #eef3f8
	}

	.console-shell aside {
		position: fixed;
		inset: 0 auto 0 0;
		width: 246px;
		padding: 27px 20px;
		background: var(--ink-950);
		color: #d9e5f2;
		z-index: 20;
		transition: .25s
	}

	.console-shell.collapsed aside {
		width: 82px
	}

	.console-shell.collapsed aside :deep(.brand>span:last-child),
	.console-shell.collapsed aside nav span,
	.console-shell.collapsed .signal span {
		display: none
	}

	.signal {
		display: flex;
		align-items: center;
		gap: 9px;
		margin: 32px 6px;
		color: #7790a9;
		font: 9px var(--mono);
		letter-spacing: .15em
	}

	.signal i {
		width: 7px;
		height: 7px;
		border-radius: 50%;
		background: var(--lime);
		box-shadow: 0 0 10px var(--lime)
	}

	nav {
		display: grid;
		gap: 7px
	}

	nav a {
		display: flex;
		align-items: center;
		gap: 14px;
		padding: 12px 13px;
		border-radius: 12px;
		color: #8ea2b8;
		font-size: 13px;
		font-weight: 700
	}

	nav a b {
		width: 26px;
		text-align: center;
		font: 19px var(--mono)
	}

	nav a.router-link-active {
		color: #fff;
		background: linear-gradient(110deg, rgba(48, 217, 255, .16), rgba(124, 108, 255, .2));
		box-shadow: inset 3px 0 var(--cyan)
	}

	.collapse {
		position: absolute;
		bottom: 24px;
		left: 20px;
		right: 20px;
		border: 1px solid rgba(255, 255, 255, .1);
		background: transparent;
		color: #7890a8;
		border-radius: 10px;
		padding: 9px
	}

	.workspace {
		margin-left: 246px;
		transition: .25s
	}

	.collapsed .workspace {
		margin-left: 82px
	}

	header {
		height: 84px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 34px;
		background: rgba(255, 255, 255, .72);
		border-bottom: 1px solid var(--line);
		backdrop-filter: blur(14px)
	}

	header>div:first-of-type {
		display: grid;
		gap: 4px
	}

	header strong {
		font-size: 18px
	}

	.operator {
		display: flex;
		align-items: center;
		gap: 12px
	}

	.operator span {
		display: grid;
		text-align: right;
		font-size: 12px
	}

	.operator small {
		color: var(--muted)
	}

	.operator i {
		width: 38px;
		height: 38px;
		display: grid;
		place-items: center;
		border-radius: 12px;
		background: var(--ink-950);
		color: var(--cyan);
		font: 11px var(--mono)
	}

	.logout {
		display: flex;
		align-items: center;
		gap: 7px;
		padding: 8px 11px;
		border: 1px solid rgba(8, 22, 39, .12);
		border-radius: 10px;
		background: rgba(255, 255, 255, .68);
		color: var(--ink-950);
		font-size: 11px;
		font-weight: 700;
		cursor: pointer;
		transition: .2s ease
	}

	.logout:hover {
		border-color: rgba(255, 111, 145, .55);
		background: rgba(255, 111, 145, .08);
		color: #d94f72;
		transform: translateY(-1px)
	}

	.logout:disabled {
		opacity: .55;
		cursor: wait;
		transform: none
	}

	.logout b {
		font: 13px var(--mono)
	}

	main {
		padding: 32px;
		max-width: 1600px;
		margin: auto
	}

	.mobile-menu {
		display: none
	}

	@media(max-width:760px) {
		.console-shell aside {
			transform: translateX(-100%);
			width: 246px
		}

		.console-shell.collapsed aside {
			transform: none;
			width: 246px
		}

		.console-shell.collapsed aside :deep(.brand>span:last-child),
		.console-shell.collapsed aside nav span,
		.console-shell.collapsed .signal span {
			display: initial
		}

		.workspace,
		.collapsed .workspace {
			margin-left: 0
		}

		.mobile-menu {
			display: block;
			border: 0;
			background: none;
			font-size: 22px
		}

		header {
			height: 70px;
			padding: 0 16px
		}

		header .eyebrow,
		.operator-info,
		.logout span {
			display: none
		}

		main {
			padding: 18px
		}
	}
</style>
