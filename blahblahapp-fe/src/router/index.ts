import { createRouter, createWebHistory } from 'vue-router';
import { Home, MessageCircle, MessagesSquare, Users } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import AuthLoginView from '@/views/AuthLoginView.vue';
import AuthRegisterView from '@/views/AuthRegisterView.vue';
import LegalTermsOfService from '@/views/LegalTermsOfService.vue';
import LegalPrivacyPolicy from '@/views/LegalPrivacyPolicy.vue';
import UserHomeView from '@/views/UserHomeView.vue';
import UserChatView from '@/views/UserChatView.vue';
import UserChannelView from '@/views/UserChannelView.vue';
import UserProfileView from '@/views/UserProfileView.vue';
import PageNotFound from '@/views/PageNotFound.vue';

const routes = [
  {
    path: '/auth/login',
    name: 'login',
    component: AuthLoginView,
    meta: {
      title: 'Login',
      isPublic: true,
    }
  },
  {
    path: '/auth/register',
    name: 'register',
    component: AuthRegisterView,
    meta: {
      title: 'Register',
      isPublic: true,
    }
  },
  {
    path: '/terms',
    name: 'terms',
    component: LegalTermsOfService,
    meta: {
      title: 'Terms of Service',
      isPublic: true,
    }
  },
  {
    path: '/privacy',
    name: 'privacy',
    component: LegalPrivacyPolicy,
    meta: {
      title: 'Privacy Policy',
      isPublic: true,
    }
  },
  {
    path: '/',
    name: 'home',
    component: UserHomeView,
    meta: {
      title: 'Dashboard',
      icon: Home,
    }
  },
  {
    path: '/chats',
    name: 'chat-list',
    component: UserChatView,
    meta: {
      title: 'Conversations',
      icon: MessageCircle,
    },
    children: [
      {
        path: ':chatId',
        name: 'chat-details',
        component: UserChatView,
        props: true,
        meta: {
          title: 'Open Chat',
        }
      }
    ]
  },
  {
    path: '/channels',
    name: 'channel-list',
    component: UserChannelView,
    meta: {
      title: 'Channels',
      icon: MessagesSquare,
    },
    children: [
      {
        path: ':channelId',
        name: 'channel-details',
        component: UserChannelView,
        props: true,
        meta: {
          title: 'Open Channel',
        }
      }
    ]
  },
  {
    path: '/profile',
    name: 'my-profile',
    component: UserProfileView,
    meta: {
      title: 'My Profile',
      icon: Users,
    },
    children: [
      {
        path: ':profileId',
        name: 'user-profile',
        component: UserProfileView,
        props: true,
        meta: {
          title: 'User Profile',
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: PageNotFound,
    meta: {
      title: 'Page Not Found',
      isPublic: true,
    }
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach((to) => {
  const { name, meta: { title, isPublic } } = to;
  const isAuthenticated = !!useUserStore().token;

  if (!isAuthenticated && !isPublic) {
    return { name: 'login' };
  }

  if (isAuthenticated && (name === 'login' || name === 'register')) {
    return { name: 'home' };
  }

  if (typeof title === 'string') {
    document.title = `BlahBlah | ${title}`;
  }
});

export default router;
