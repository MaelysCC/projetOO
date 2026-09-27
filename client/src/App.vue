<script setup>
import { ref, computed, onMounted } from "vue";
import { api } from "./services/api";

const works = ref([]);
const users = ref([]);
const entries = ref([]);

const currentUser = ref(null);
const activePage = ref("catalog");

const search = ref("");
const filterType = ref("ALL");
const filterStatus = ref("ALL");

const loading = ref(false);
const error = ref("");
const message = ref("");

const showAddWork = ref(false);
const showAddUser = ref(false);

const newWork = ref({
  title: "",
  type: "MANGA",
  author: "",
  description: "",
  status: "ONGOING"
});

const newUser = ref({
  username: "",
  email: ""
});

const workTypes = [
  "NOVEL",
  "WEBCOMIC",
  "MANGA",
  "ANIME"
];

const statuses = [
  "PLANNED",
  "ONGOING",
  "ON_HOLD",
  "DROPPED",
  "COMPLETED"
];

const statusLabels = {
  PLANNED: "Planned",
  ONGOING: "Ongoing",
  ON_HOLD: "On hold",
  DROPPED: "Dropped",
  COMPLETED: "Completed"
};

const typeLabels = {
  NOVEL: "Novel",
  WEBCOMIC: "Webcomic",
  MANGA: "Manga",
  ANIME: "Anime"
};

const stats = computed(() => ({
  total: entries.value.length,
  ongoing: entries.value.filter(e => e.status === "ONGOING").length,
  completed: entries.value.filter(e => e.status === "COMPLETED").length,
  planned: entries.value.filter(e => e.status === "PLANNED").length
}));

const filteredWorks = computed(() => {
  return works.value.filter(work => {
    const title = work.title || "";
    const author = work.author || "";

    const matchesSearch =
      title.toLowerCase().includes(search.value.toLowerCase()) ||
      author.toLowerCase().includes(search.value.toLowerCase());

    const matchesType =
      filterType.value === "ALL" ||
      work.type === filterType.value;

    const entry = getEntry(work.id);

    const matchesStatus =
      filterStatus.value === "ALL" ||
      entry?.status === filterStatus.value;

    let matchesPage = true;

    if (activePage.value === "mylist") {
      matchesPage = !!entry;
    }

    if (activePage.value === "ongoing") {
      matchesPage = entry?.status === "ONGOING";
    }

    if (activePage.value === "completed") {
      matchesPage = entry?.status === "COMPLETED";
    }

    return (
      matchesSearch &&
      matchesType &&
      matchesStatus &&
      matchesPage
    );
  });
});

function getEntry(workId) {
  return entries.value.find(entry => 
    Number(entry.workId) === Number(workId)
  );
}

function getProgress(entry) {
  return Number(entry?.currentChapter || 0);
}

function showMessage(text) {
  message.value = text;

  setTimeout(() => {
    message.value = "";
  }, 2500);
}

async function loadData() {
  loading.value = true;
  error.value = "";

  try {
    const [workData, userData] = await Promise.all([
      api.getWorks(),
      api.getUsers()
    ]);

    works.value = workData;
    users.value = userData;

    const savedUser = localStorage.getItem("seriesTrackerUser");

    if (savedUser) {
      const user = users.value.find(
        user => String(user.id) === savedUser
      );

      if (user) {
        currentUser.value = user;
        await loadEntries();
      }
    }
  } catch (e) {
    console.error(e);
    error.value = "Impossible de contacter le serveur.";
  } finally {
    loading.value = false;
  }
}

async function loadEntries() {
  if (!currentUser.value) {
    return;
  }

  try {
    entries.value = await api.getEntries(
      currentUser.value.id
    );
  } catch (e) {
    console.error(e);
    error.value = "Impossible de charger votre liste.";
  }
}

async function login(user) {
  currentUser.value = user;

  localStorage.setItem(
    "seriesTrackerUser",
    String(user.id)
  );

  await loadEntries();
}

function logout() {
  currentUser.value = null;
  entries.value = [];

  localStorage.removeItem("seriesTrackerUser");
}

async function createUser() {
  if (!newUser.value.username.trim()) {
    return;
  }

  try {
    const user = await api.addUser({
      username: newUser.value.username,
      email: newUser.value.email
    });

    users.value.push(user);

    await login(user);

    newUser.value = {
      username: "",
      email: ""
    };

    showAddUser.value = false;

    showMessage("Account created!");
  } catch (e) {
    console.error(e);
    error.value = e.message;
  }
}

async function createWork() {
  if (!newWork.value.title.trim()) {
    return;
  }

  try {
    const work = await api.addWork({
      ...newWork.value
    });

    works.value.push(work);

    newWork.value = {
      title: "",
      type: "MANGA",
      author: "",
      description: "",
      status: "ONGOING"
    };

    showAddWork.value = false;

    showMessage("Work added!");
  } catch (e) {
    console.error(e);
    error.value = e.message;
  }
}

async function addToList(work) {
  if (!currentUser.value) {
    showMessage("Please select a user first.");
    return;
  }

  if (getEntry(work.id)) {
    showMessage("This work is already in your list.");
    return;
  }

  try {
    const entry = await api.addEntry(
    currentUser.value.id,  
    {
      workId: work.id,
      status: "PLANNED",
      currentChapter: 0,
      rating: 0
    });

    entries.value.push(entry);

    showMessage("Added to your list!");
  } catch (e) {
    console.error(e);
    error.value = e.message;
  }
}

async function updateEntry(entry, field, value) {
  try {
    const updatedEntry = {
      ...entry,
      [field]: value
    };

    const result = await api.updateEntry(
      entry.id,
      updatedEntry
    );

    const index = entries.value.findIndex(
      e => e.id === entry.id
    );

    if (index !== -1) {
      entries.value[index] = result;
    }

    showMessage("Updated!");
  } catch (e) {
    console.error(e);
    error.value = e.message;
  }
}

async function removeEntry(entry) {
  if (!confirm("Remove this work from your list?")) {
    return;
  }

  try {
    await api.deleteEntry(entry.id);

    entries.value = entries.value.filter(
      e => e.id !== entry.id
    );

    showMessage("Removed from your list.");
  } catch (e) {
    console.error(e);
    error.value = e.message;
  }
}

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="app">

    <!-- SIDEBAR -->
    <aside class="sidebar">

      <div class="brand">
        <span class="brand-icon">S</span>
        <span>SeriesTracker</span>
      </div>

      <div class="user-box">

        <template v-if="currentUser">

          <div class="avatar">
            {{ currentUser.username.charAt(0).toUpperCase() }}
          </div>

          <div class="user-info">
            <strong>{{ currentUser.username }}</strong>
            <small>Personal library</small>
          </div>

          <button
            class="icon-button"
            title="Logout"
            @click="logout"
          >
            ↪
          </button>

        </template>

        <template v-else>

          <button
            class="primary-button full"
            @click="showAddUser = true"
          >
            Create an account
          </button>

        </template>

      </div>

      <nav>

        <p class="nav-label">
          LIBRARY
        </p>

        <button
          :class="{ active: activePage === 'catalog' }"
          @click="activePage = 'catalog'"
        >
          <span>⌕</span>
          Discover
        </button>

        <button
          :class="{ active: activePage === 'mylist' }"
          @click="activePage = 'mylist'"
        >
          <span>▤</span>
          My List
        </button>

        <button
          :class="{ active: activePage === 'ongoing' }"
          @click="activePage = 'ongoing'"
        >
          <span>◷</span>
          Currently Reading
        </button>

        <button
          :class="{ active: activePage === 'completed' }"
          @click="activePage = 'completed'"
        >
          <span>✓</span>
          Completed
        </button>

      </nav>

      <div class="sidebar-bottom">
        <span class="status-dot"></span>
        Series Tracker
        <small>v1.0</small>
      </div>

    </aside>

    <!-- MAIN -->
    <main class="main">

      <!-- TOP BAR -->
      <header class="topbar">

        <div>
          <span class="breadcrumb">
            Library /
          </span>

          <strong>
            {{
              activePage === "catalog"
                ? "Discover"
                : activePage === "mylist"
                  ? "My List"
                  : activePage === "ongoing"
                    ? "Currently Reading"
                    : "Completed"
            }}
          </strong>
        </div>

        <button
          v-if="currentUser"
          class="primary-button"
          @click="showAddWork = true"
        >
          + Add a work
        </button>

      </header>

      <!-- CONTENT -->
      <section class="content">

        <div class="welcome">

          <p class="eyebrow">
            YOUR PERSONAL TRACKER
          </p>

          <h1>
            {{
              currentUser
                ? `Welcome back, ${currentUser.username}.`
                : "Track your favorite stories."
            }}
          </h1>

          <p class="subtitle">
            Keep track of your novels, webcomics,
            manga and anime.
          </p>

        </div>

        <!-- STATS -->
        <div
          v-if="currentUser"
          class="stats"
        >

          <div class="stat-card">
            <span>Total tracked</span>
            <strong>{{ stats.total }}</strong>
          </div>

          <div class="stat-card">
            <span>In progress</span>
            <strong>{{ stats.ongoing }}</strong>
          </div>

          <div class="stat-card">
            <span>Completed</span>
            <strong>{{ stats.completed }}</strong>
          </div>

          <div class="stat-card">
            <span>Planned</span>
            <strong>{{ stats.planned }}</strong>
          </div>

        </div>

        <!-- ERROR -->
        <div
          v-if="error"
          class="error-message"
        >
          {{ error }}
        </div>

        <!-- LOADING -->
        <div
          v-if="loading"
          class="empty"
        >
          Loading...
        </div>

        <template v-else>

          <!-- TOOLBAR -->
          <div class="toolbar">

            <div class="search-box">

              <span>⌕</span>

              <input
                v-model="search"
                placeholder="Search by title or author..."
              />

            </div>

            <select v-model="filterType">

              <option value="ALL">
                All types
              </option>

              <option
                v-for="type in workTypes"
                :key="type"
                :value="type"
              >
                {{ typeLabels[type] }}
              </option>

            </select>

            <select v-model="filterStatus">

              <option value="ALL">
                All statuses
              </option>

              <option
                v-for="status in statuses"
                :key="status"
                :value="status"
              >
                {{ statusLabels[status] }}
              </option>

            </select>

          </div>

          <!-- TITLE -->
          <div class="section-heading">

            <div>

              <h2>
                {{
                  activePage === "catalog"
                    ? "Explore works"
                    : "Your library"
                }}
              </h2>

              <p>
                {{ filteredWorks.length }} works found
              </p>

            </div>

          </div>

          <!-- WORKS -->
          <div
            v-if="filteredWorks.length"
            class="work-grid"
          >

            <article
              v-for="work in filteredWorks"
              :key="work.id"
              class="work-card"
            >

              <div class="cover">

                <span>
                  {{ typeLabels[work.type] }}
                </span>

                <strong>
                  {{ work.title.charAt(0).toUpperCase() }}
                </strong>

              </div>

              <div class="work-details">

                <div class="work-type">
                  {{ typeLabels[work.type] }}
                </div>

                <h3>
                  {{ work.title }}
                </h3>

                <p class="author">
                  {{ work.author || "Unknown author" }}
                </p>

                <p class="description">
                  {{
                    work.description ||
                    "No description available."
                  }}
                </p>

                <!-- ENTRY -->
                <template v-if="getEntry(work.id)">

                  <!-- STATUS -->
                  <div class="entry-status">

                    <label>
                      Status
                    </label>

                    <select
                      :value="getEntry(work.id).status"
                      @change="
                        updateEntry(
                          getEntry(work.id),
                          'status',
                          $event.target.value
                        )
                      "
                    >

                      <option
                        v-for="status in statuses"
                        :key="status"
                        :value="status"
                      >
                        {{ statusLabels[status] }}
                      </option>

                    </select>

                  </div>

                  <!-- PROGRESS -->
                  <div class="progress">

                    <div class="progress-header">

                      <label>
                        Chapter / Episode
                      </label>

                      <strong>
                        {{ getProgress(getEntry(work.id)) }}
                      </strong>

                    </div>

                    <input
                      type="number"
                      min="0"
                      :value="getProgress(getEntry(work.id))"
                      @change="
                        updateEntry(
                          getEntry(work.id),
                          'currentChapter',
                          Number($event.target.value)
                        )
                      "
                    />

                  </div>

                  <!-- RATING -->
                  <div class="rating">

                    <label>
                      Your rating
                    </label>

                    <select
                      :value="
                        getEntry(work.id).rating ?? ''
                      "
                      @change="
                        updateEntry(
                          getEntry(work.id),
                          'rating',
                          $event.target.value === ''
                            ? null
                            : Number($event.target.value)
                        )
                      "
                    >

                      <option value="">
                        Not rated
                      </option>

                      <option
                        v-for="n in 5"
                        :key="n"
                        :value="n"
                      >
                        {{ n }} / 5
                      </option>

                    </select>

                  </div>

                  <button
                    class="secondary-button full"
                    @click="removeEntry(getEntry(work.id))"
                  >
                    Remove from my list
                  </button>

                </template>

                <!-- ADD -->
                <button
                  v-else
                  class="primary-button full"
                  :disabled="!currentUser"
                  @click="addToList(work)"
                >
                  + Add to my list
                </button>

              </div>

            </article>

          </div>

          <!-- EMPTY -->
          <div
            v-else
            class="empty"
          >

            <h3>
              No works found
            </h3>

            <p>
              Try changing your filters or adding a new work.
            </p>

          </div>

        </template>

      </section>

    </main>

    <!-- ADD WORK MODAL -->
    <div
      v-if="showAddWork"
      class="modal-overlay"
      @click.self="showAddWork = false"
    >

      <form
        class="modal"
        @submit.prevent="createWork"
      >

        <div class="modal-header">

          <h2>
            Add a work
          </h2>

          <button
            type="button"
            class="icon-button"
            @click="showAddWork = false"
          >
            ✕
          </button>

        </div>

        <label>
          Title *
        </label>

        <input
          v-model="newWork.title"
          required
          placeholder="Work title"
        />

        <label>
          Type
        </label>

        <select v-model="newWork.type">

          <option
            v-for="type in workTypes"
            :key="type"
            :value="type"
          >
            {{ typeLabels[type] }}
          </option>

        </select>

        <label>
          Author
        </label>

        <input
          v-model="newWork.author"
          placeholder="Author"
        />

        <label>
          Description
        </label>

        <textarea
          v-model="newWork.description"
          rows="3"
        ></textarea>

        <label>
          Work status
        </label>

        <select v-model="newWork.status">

          <option
            v-for="status in statuses"
            :key="status"
            :value="status"
          >
            {{ statusLabels[status] }}
          </option>

        </select>

        <button
          class="primary-button full"
          type="submit"
        >
          Save work
        </button>

      </form>

    </div>

    <!-- CREATE USER MODAL -->
    <div
      v-if="showAddUser"
      class="modal-overlay"
      @click.self="showAddUser = false"
    >

      <form
        class="modal"
        @submit.prevent="createUser"
      >

        <div class="modal-header">

          <h2>
            Create an account
          </h2>

          <button
            type="button"
            class="icon-button"
            @click="showAddUser = false"
          >
            ✕
          </button>

        </div>

        <label>
          Username *
        </label>

        <input
          v-model="newUser.username"
          required
          placeholder="Your username"
        />

        <label>
          Email
        </label>

        <input
          v-model="newUser.email"
          type="email"
          placeholder="your@email.com"
        />

        <button
          class="primary-button full"
          type="submit"
        >
          Create account
        </button>

        <div
          v-if="users.length"
          class="existing-users"
        >

          <p>
            Or select an existing user:
          </p>

          <button
            v-for="user in users"
            :key="user.id"
            type="button"
            class="secondary-button full"
            @click="
              login(user);
              showAddUser = false
            "
          >
            {{ user.username }}
          </button>

        </div>

      </form>

    </div>

    <!-- TOAST -->
    <div
      v-if="message"
      class="toast"
    >
      {{ message }}
    </div>

  </div>
</template>