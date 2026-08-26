<script setup>
	import BrandMark from '../components/BrandMark.vue'
</script>
<template>
	<div class="app-shell">
		<header><router-link to="/app/explore">
				<BrandMark />
			</router-link>
			<nav><router-link to="/app/explore">发现</router-link><router-link
					to="/app/categories">声场</router-link><router-link to="/app/account">我的</router-link></nav>
			<router-link class="studio-link" to="/console/dashboard">进入控制台 <span>↗</span></router-link>
		</header>
		<main><router-view /></main>
		<footer>
			<BrandMark />
			<p>把每一次发现，收进一张音乐卡片。</p><span>© 2026 MUSICCARD</span>
		</footer>
		<nav class="mobile-nav"><router-link to="/app/explore">◉<small>发现</small></router-link><router-link
				to="/app/categories">◇<small>声场</small></router-link><router-link
				to="/app/account">○<small>我的</small></router-link></nav>
	</div>
</template>
<style scoped>
	.app-shell {
		min-height: 100vh;
		background: radial-gradient(circle at 8% 4%, rgba(48, 217, 255, .15), transparent 24%), radial-gradient(circle at 92% 14%, rgba(124, 108, 255, .12), transparent 22%), var(--paper)
	}

	header {
		position: sticky;
		top: 0;
		z-index: 30;
		height: 76px;
		display: grid;
		grid-template-columns: 1fr auto 1fr;
		align-items: center;
		padding: 0 clamp(20px, 5vw, 72px);
		background: rgba(244, 247, 251, .78);
		border-bottom: 1px solid var(--line);
		backdrop-filter: blur(18px)
	}

	nav {
		display: flex;
		gap: 34px;
		font-size: 14px;
		font-weight: 700
	}

	nav a {
		position: relative;
		padding: 8px
	}

	.router-link-active:not(.studio-link):after {
		content: "";
		position: absolute;
		left: 8px;
		right: 8px;
		bottom: 0;
		height: 2px;
		background: var(--violet)
	}

	.studio-link {
		justify-self: end;
		font: 12px var(--mono);
		padding: 10px 14px;
		border: 1px solid var(--line);
		border-radius: 999px
	}

	.studio-link span {
		color: var(--violet)
	}

	main {
		min-height: calc(100vh - 76px)
	}

	footer {
		display: flex;
		align-items: center;
		gap: 32px;
		margin: 50px clamp(20px, 5vw, 72px) 0;
		padding: 34px 0 42px;
		border-top: 1px solid var(--line);
		color: var(--muted);
		font-size: 12px
	}

	footer p {
		flex: 1
	}

	.mobile-nav {
		display: none
	}

	@media(max-width:720px) {
		header {
			height: 64px;
			display: flex;
			justify-content: space-between;
			padding: 0 18px
		}

		header nav,
		.studio-link {
			display: none
		}

		main {
			padding-bottom: 74px
		}

		footer {
			margin: 20px 20px 74px;
			display: grid;
			gap: 10px
		}

		.mobile-nav {
			position: fixed;
			display: grid;
			grid-template-columns: repeat(3, 1fr);
			bottom: 12px;
			left: 12px;
			right: 12px;
			z-index: 50;
			padding: 7px;
			background: rgba(7, 17, 31, .93);
			border: 1px solid rgba(255, 255, 255, .15);
			border-radius: 19px;
			backdrop-filter: blur(18px);
			box-shadow: 0 16px 35px rgba(7, 17, 31, .3)
		}

		.mobile-nav a {
			display: grid;
			place-items: center;
			gap: 3px;
			color: #9aa9bc;
			padding: 5px
		}

		.mobile-nav small {
			font-size: 10px
		}

		.mobile-nav .router-link-active {
			color: var(--cyan)
		}

		.mobile-nav .router-link-active:after {
			display: none
		}
	}
</style>